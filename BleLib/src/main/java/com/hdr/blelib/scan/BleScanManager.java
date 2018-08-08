package com.hdr.blelib.scan;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;


/**
 * 自定义蓝牙扫描管理器
 * Created by hdr on 17/4/14.
 */

public abstract class BleScanManager {


    protected volatile boolean isScanning = false;

    protected BluetoothAdapter bluetoothAdapter;
    protected final Handler mHandler;
    private static BleScanManager instance;

    public static BleScanManager getInstance(Context context) {
        if (instance == null) {
            instance = new BleScanManagerImpl(context);
        }
        return instance;
    }

    protected BleScanManager(Context context) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void startScan(ScanCallback callback) {
    }

    public void stopScan(ScanCallback callback) {
    }

    abstract void internalStartScan(ScanCallback callback);

    abstract void internalStopScan();
}
