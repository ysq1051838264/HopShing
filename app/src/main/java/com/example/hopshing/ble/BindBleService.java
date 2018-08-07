package com.example.hopshing.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kingnew.foreign.domain.other.log.LogUtils;

import no.nordicsemi.android.nrftoolbox.profile.BleManager;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileService;

/**
 * Created by hdr on 16/3/16.
 */
public class BindBleService extends BleProfileService implements BindBleManager.BindManagerCallback {
    public static final String BROADCAST_BIND_READ_NAME = "com.hdr.bind_read_name";
    public static final String BROADCAST_BIND_READ_INTERNAL_MODEL = "com.hdr.bind_read_internal_model";
    public static final String BROADCAST_BIND_BLE_ERROR = "com.hdr.broadcast_bind_ble_error";

    public static final String EXTRA_BIND_NAME = "extra_bind_name";
    public static final String EXTRA_BIND_INTERNAL_MODEL = "extra_bind_internal_model";

    public static final int TYPE_OP_ERROR = -1;
    public static final int TYPE_OP_READ_SCALE_NAME = 0;
    public static final int TYPE_OP_READ_INTERNAL_MODEL = 1;

    private boolean success;

    BindBleManager bindBleManager;

    @Override
    protected BleManager initializeManager() {
        return bindBleManager = new BindBleManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || !intent.hasExtra(EXTRA_DEVICE_ADDRESS))
            throw new UnsupportedOperationException("No device address at EXTRA_DEVICE_ADDRESS key");
        mDeviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS);

        final Intent broadcast = new Intent(BROADCAST_CONNECTION_STATE);
        broadcast.putExtra(EXTRA_CONNECTION_STATE, STATE_CONNECTING);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = bluetoothManager.getAdapter();
        final BluetoothDevice device = adapter.getRemoteDevice(mDeviceAddress);
        onServiceStarted();
        Log.e("蓝牙日志：","尝试连接读取型号: " + mDeviceAddress);
        bindBleManager.connect(device);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onError(String message, int errorCode) {
        Log.e("蓝牙日志：","出现蓝牙错误:"+errorCode+message);

    }

    @Override
    public void onDeviceDisconnected() {
        if (!success) {
            Log.e("蓝牙日志：","未成功读取到型号或名称");
            Intent intent = new Intent(BROADCAST_BIND_BLE_ERROR);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            stopSelf();
        }
    }

    @Override
    public void onNameRead(String address, String name) {
        Log.e("蓝牙日志：","读取到蓝牙名称:"+address+name);
        Intent intent = new Intent(BROADCAST_BIND_READ_NAME);
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        intent.putExtra(EXTRA_BIND_NAME, name);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        success = true;
        stopSelf();
    }

    @Override
    public void onInternalModelRead(String address, String internalModel) {
        Log.e("蓝牙日志：","读取到内部型号:"+ address+internalModel);
        Intent intent = new Intent(BROADCAST_BIND_READ_INTERNAL_MODEL);
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        intent.putExtra(EXTRA_BIND_INTERNAL_MODEL, internalModel);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        success = true;
        stopSelf();
    }
}
