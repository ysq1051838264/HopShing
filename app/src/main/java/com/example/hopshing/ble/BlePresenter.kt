package com.example.hopshing.ble;


import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.net.ConnectivityManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.example.hopshing.InitDataPresenter
import com.example.hopshing.LifeCyclePresenter
import com.example.hopshing.utils.BleConst
import com.hdr.blelib.utils.BleUtils
import org.jetbrains.anko.async
import org.jetbrains.anko.toast
import java.util.*

/**
 * 蓝牙称重实现类
 * Created by hr on 2015/12/10.
 */
class BlePresenter(val bleView: BleView, val activity: Activity) : BroadcastReceiver(), LifeCyclePresenter, InitDataPresenter, DecoderAdapter {

    internal var curUser = CurUser

    internal var spHelper = SpHelper.getInstance()
    internal var bleCase = BleCase()

    internal var userListCase = UserListCase()

    internal var decoder: Decoder? = null

    internal var isVisible = false

    private var connectedDevice: KingNewDeviceModel? = null

    val devices: List<KingNewDeviceModel>
        get() = bleCase.allDevice


    internal var bleService: ScaleBleService? = null
    internal var uiHandler = Handler(Looper.getMainLooper())

    internal var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            bleService = (service as ScaleBleService.ScaleBleBinder).service
            uiHandler.removeCallbacks(remindLocationSettingAction)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            bleService = null
        }
    }

    override fun initData() {

        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BleConst.ACTION_BLE_RECEIVE_DATA)
        intentFilter.addAction(BleConst.ACTION_BLE_CONNECTED)
        intentFilter.addAction(BleConst.ACTION_BLE_DISCONNECTED)
        intentFilter.addAction(BleConst.ACTION_BLE_DISCOVERED)

        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

        LocalBroadcastManager.getInstance(bleView.ctx).registerReceiver(this, intentFilter)

        bleView.ctx.bindService(Intent(bleView.ctx, ScaleBleService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)

        connectedDevice = null
    }

    /**
     * 连接指定的设备
     */
    fun connectDevice(scanDevice: ScanDevice): Boolean {
        LogUtils.saveBleLog("connectedDevice 的值 ", connectedDevice)
        if (bleService == null || connectedDevice != null) {
            return false
        }
        //判断当前是否已经有绑定设备，并且这个设备是否处于绑定设备列表中
        val connectFlag: Boolean
        val bindFlag: Boolean
        val devices = this.devices
        if (devices.isEmpty()) {
            connectFlag = true
            bindFlag = true
            val device = KingNewDeviceModel()

            device.mac = scanDevice.mac
            device.internalModel = scanDevice.internalModel
            device.scaleName = scanDevice.scaleName

            connectedDevice = device
            LogUtils.saveBleLog("connectedDevice 进行了赋值", connectedDevice)
        } else if (scanDevice.hasBind) {
            connectFlag = true
            bindFlag = false
            connectedDevice = bleCase.getDeviceWithMac(scanDevice.mac)
            LogUtils.saveBleLog("connectedDevice 进行了赋值", connectedDevice)
        } else {
            return false
        }
        val connectedDevice = connectedDevice
        uiHandler.postDelayed({
            async() {
                bleService!!.connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(connectedDevice?.mac), connectedDevice?.deviceInfo)
            }
        }, 150)
        if (bindFlag) {
            bleCase.saveBindKingNewDevice(connectedDevice).subscribe(DefaultSubscriber<Any>())
        }
        return connectFlag

    }

    fun canStartScan() = this.connectedDevice == null


    fun onConnected(address: String) {
        if (!checkCurrentDevice(address)) {
            return
        }
        bleView.onConnected()
    }

    fun onDisconnected(address: String) {
        decoder = null
        connectedDevice = null
        LogUtils.saveBleLog("蓝牙断开连接")
        bleView.onDisconnected()
    }

    internal fun checkCurrentDevice(address: String): Boolean {
        if (connectedDevice == null || connectedDevice!!.mac != address) {
            if (bleService != null) {
                bleService!!.disconnect()
            }
            return false
        }
        return true
    }

    internal fun prepareDecoder() {
        decoder?.let {
            decoder ->
            decoder.setDeviceModel(connectedDevice)
            decoder.setCurUser(curUser)
            decoder.setDecoderAdapter(this)
        }
    }

    fun initBleLogo() {
        if (BleUtils.isEnable(bleView.ctx)) {
            //蓝牙变为了可用
            bleView.onBleEnable()
        } else {
            bleView.onBleDisabled()
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) {
            return
        }
        val action = intent.action
        when (action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                //蓝牙开关发生了变化
                initBleLogo()
            }
            BleConst.ACTION_BLE_CONNECTED -> {
                val address = intent.getStringExtra(BleConst.KEY_MAC)
                onConnected(address)
            }
            BleConst.ACTION_BLE_DISCONNECTED -> {
                val address = intent.getStringExtra(BleConst.KEY_MAC)
                onDisconnected(address)
            }
            BleConst.ACTION_BLE_DISCOVERED -> {
                val address = intent.getStringExtra(BleConst.KEY_MAC)
                onServiceDiscovered(address)
            }
            BleConst.ACTION_BLE_RECEIVE_DATA -> {
                val uuid = intent.getSerializableExtra(BleConst.KEY_UUID) as UUID
                val data = intent.getByteArrayExtra(BleConst.KEY_DATA)
                onReceivedData(uuid, data)
            }
            ConnectivityManager.CONNECTIVITY_ACTION -> {
            }

        }
    }

    private fun onServiceDiscovered(address: String) {
        if (!checkCurrentDevice(address)) {
            return
        }
        LogUtils.saveBleLog("设备初始化成功")
        //读称的名称
        when (connectedDevice!!.deviceInfo.decoderType) {
            3 -> decoder = if (connectedDevice!!.deviceInfo.scaleName == BleConst.SCALE_NAME_CS30C) CS30CDecoder() else WeightScaleDecoder()
            6 -> decoder = MiScaleDecoder()
        }
        prepareDecoder()
    }

    private fun onReceivedData(uuid: UUID, data: ByteArray) {
        Log.e("收到数据:", byteArrayToString(data))

        if (decoder!!.receiveData(uuid, data, bleView.ctx)) {
            bleView.onStartTransfer()
        }
    }

    private fun byteArrayToString(data: ByteArray): String {
        val sb = StringBuilder()
        for (b in data) {
            sb.append(String.format("%02X ", b))
        }
        return sb.toString()
    }


    override fun resume() {
        isVisible = true
        this.connectedDevice = null
        initBleLogo()
    }

    override fun pause() {
        isVisible = false
        uiHandler.removeCallbacks(remindLocationSettingAction)
        if (bleService != null)
            bleService!!.disconnect()
    }

    override fun destroy() {
        LocalBroadcastManager.getInstance(bleView.ctx).unregisterReceiver(this)
        try {
            bleView.ctx.unbindService(serviceConnection)
        } catch (e: Exception) {
        }

    }

    override fun onLowPower() {
        bleView.onDeviceLowPower()
    }

    override fun writeData(serviceUUID: UUID, characteristicUUID: UUID, value: ByteArray) {
        if (bleService == null) {
            return
        }
        bleService!!.writeData(serviceUUID, characteristicUUID, value)
    }


    override fun onBeginReceiveStorageData() {

    }


    fun saveStorageData(measuredDataModels: List<MeasuredDataModel>) {

        // 体重大于250,不断循环
        measuredDataModels.forEach {
            if (it.weight > 250) {
                it.weight = it.weight / 10
            }
        }

        MeasuredDataStore.uploadMeasuredData(bleView.ctx, curUser.curUser!!.serverId, measuredDataModels)
        var newestData: MeasuredDataModel? = bleView.curReportData?.md

        measuredDataModels.forEach {

            // 上传数据到googlefit
            if (spHelper.getBoolean(SystemConst.GOOGLE_FIT_SWITCH, false) && curUser.isMaster) {
                GoogleFit.insertData(it.weight, fragmentActivity!!)
            }

            if (spHelper.getBoolean(SystemConst.FIT_BIT_SWITCH, false) && curUser.isMaster) {
                FitBit.fitbitUpload("weight", it.weight, "fat", it.bodyfat)
            }

            if (it.userId == curUser.curUser!!.serverId && (newestData?.timeStamp ?: 0 < it.timeStamp)) {
                newestData = it
            }
        }
        newestData?.let {
            val deviceInfoModel: DeviceInfoModel = bleCase.getDeviceInfoWithScaleName(newestData?.scaleName, newestData?.internalModel)
            if (connectedDevice != null) {
                connectedDevice?.deviceInfo
            }
            val reportData = ReportData(it, deviceInfoModel, bleView.ctx)
            bleView.gotMeasuredIndicator(reportData)
        }
    }


    internal fun getClosedUsersWithLimit(mdList: List<MeasuredDataModel>, weight: Float, limit: Int): List<UserModel>? {
        val userTemps = LinkedList<CompareTemp>()
        for ((localId, serverId, scaleName, internalModel, userId, weight1) in mdList) {
            val d = Math.abs(weight1 - weight)
            val temp = CompareTemp(userId, d.toDouble())
            var i = 0
            while (i < userTemps.size && userTemps[i].weightData <= temp.weightData) {
                i++
            }
            userTemps.add(i, temp)
        }
        val userIds = ArrayList<Long>()
        var i = 0
        while (i < userTemps.size && i < limit) {
            if (userTemps[i].weightData <= MeasureConst.DIFFER_WEIGHT) {
                userIds.add(userTemps[i].userId)
            } else
                break
            i++
        }
        return if (userIds.size > 0) userListCase.getUserByServerIds(userIds) else null
    }


    private fun cancelAnim() {
        bleView.cancelAnim()
    }

    private fun showMeasuredData(md: MeasuredDataModel) {

        // 如果体重超过250,就不断循环
        while (md.weight > 250) {
            md.weight = md.weight / 10
        }
        md.uploadStatus = MeasureConst.MEASURE_DATA_NOT_UPLOAD
        // 把数据存进数据库
        MeasuredDataStore.uploadMeasuredData(bleView.ctx, md)

        // 上传数据到googlefit
        if (spHelper.getBoolean(SystemConst.GOOGLE_FIT_SWITCH, false) && curUser.isMaster) {
            GoogleFit.insertData(md.weight, this.fragmentActivity)
        }

        if (spHelper.getBoolean(SystemConst.FIT_BIT_SWITCH, false) && curUser.isMaster) {
            FitBit.fitbitUpload("weight", md.weight, "fat", md.bodyfat)
        }

        val deviceInfoModel: DeviceInfoModel = bleCase.getDeviceInfoWithScaleName(md.scaleName, md.internalModel)
        if (connectedDevice != null) {
            connectedDevice?.deviceInfo
        }
        val reportData = ReportData(md, deviceInfoModel, bleView.ctx)
        bleView.gotMeasuredIndicator(reportData)
    }

    override fun getMethod(): Int {
        return if (connectedDevice == null) 0 else connectedDevice!!.deviceInfo.method
    }

    override fun onUpdateDevice(mac: String, scaleName: String, internalModel: String) {
        if (connectedDevice != null && connectedDevice!!.mac == mac) {
            connectedDevice!!.scaleName = scaleName
            connectedDevice!!.internalModel = internalModel
            connectedDevice!!.deviceInfo = bleCase.getDeviceInfoWithScaleName(scaleName, internalModel)
            bleCase.saveBindKingNewDevice(connectedDevice).subscribe(DefaultSubscriber<Any>())
        }
    }

    private data class CompareTemp(internal var userId: Long, internal var weightData: Double)
}
