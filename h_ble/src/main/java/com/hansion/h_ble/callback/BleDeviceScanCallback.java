package com.hansion.h_ble.callback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * Created by yangshuquan on 2018/8/4.
 */
public class BleDeviceScanCallback implements BluetoothAdapter.LeScanCallback {
    private ScanCallback mScanCallback;

    public BleDeviceScanCallback(ScanCallback scanCallback) {
        this.mScanCallback = scanCallback;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (null != mScanCallback) {
            //每次扫描到设备会回调此方法,应该在此回调方法中去除重复并加入集合，用于列表展示
            mScanCallback.onScanning(device, rssi, scanRecord);
        }
    }
}