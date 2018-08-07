package com.example.hopshing.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.example.hopshing.utils.BleConst;

import java.util.UUID;
import no.nordicsemi.android.nrftoolbox.profile.BleManager;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileService;

/**
 * Created by hdr on 16/3/17.
 */
public class ScaleBleService extends BleProfileService implements ScaleMultiBleManager.ScaleBleCallback {

    ScaleMultiBleManager scaleBleManager;
    private String mDeviceAddress;
    private DeviceInfoModel deviceInfo;

    @Override
    public IBinder onBind(Intent intent) {
        return new ScaleBleBinder();
    }

    @Override
    protected BleManager initializeManager() {
        return scaleBleManager = new ScaleMultiBleManager(this);
    }

    public void connect(final BluetoothDevice device, DeviceInfoModel deviceInfo) {
        this.mDeviceAddress = device.getAddress();
        this.deviceInfo = deviceInfo;
        new Thread(new Runnable() {
            @Override
            public void run() {
                scaleBleManager.connect(device);

            }
        }).start();
    }

    public void disconnect() {
        try {
            scaleBleManager.disconnect();
        } catch (Exception e) {
        }
    }

    public void writeData(UUID serviceUUID, UUID characteristicUUID, byte[] value) {
        scaleBleManager.writeData(serviceUUID, characteristicUUID, value);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mDeviceAddress = null;
        this.deviceInfo = null;
    }

    @Override
    public void onReceiveData(UUID uuid, byte[] value) {
        Intent intent = new Intent(BleConst.ACTION_BLE_RECEIVE_DATA);
        intent.putExtra(BleConst.KEY_UUID, uuid);
        intent.putExtra(BleConst.KEY_DATA, value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @Override
    public void onServicesDiscovered(boolean optionalServicesFound) {
        Intent intent = new Intent(BleConst.ACTION_BLE_DISCOVERED);
        intent.putExtra(BleConst.KEY_MAC, mDeviceAddress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDeviceDisconnected() {
        Intent intent = new Intent(BleConst.ACTION_BLE_DISCONNECTED);
        intent.putExtra(BleConst.KEY_MAC, mDeviceAddress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDeviceConnected() {
        Intent intent = new Intent(BleConst.ACTION_BLE_CONNECTED);
        intent.putExtra(BleConst.KEY_MAC, mDeviceAddress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public void onError(String message, int errorCode) {
        Intent intent = new Intent(BleConst.ACTION_BLE_CONNECTED);
        intent.putExtra(BleConst.KEY_MAC, mDeviceAddress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public int getProtocolType() {
        return deviceInfo == null ? -1 : deviceInfo.decoderType;
    }

    @Override
    public String getCurrentAddress() {
        return mDeviceAddress;
    }

    @Override
    public boolean needReadScaleName() {
        return deviceInfo != null && BleConst.SCALE_NAME_UNKNOWN.equals(deviceInfo.scaleName);
    }

    @Override
    public boolean needReadInternalModel() {
        return deviceInfo != null && BleConst.SCALE_NAME_QINGNIU.equals(deviceInfo.scaleName) && BleConst.INTERNAL_MODEL_NORMAL.equals(deviceInfo.internalModel);
    }

    public class ScaleBleBinder extends Binder {
        public ScaleBleService getService() {
            return ScaleBleService.this;
        }
    }

}
