package com.hdr.blelib.scan;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * 蓝牙扫描管理实现类
 * Created by hdr on 17/4/15.
 */

public class BleScanManagerImpl extends BleScanManager {

    private final BleScanManager scanManagerImpl;
    private boolean cmdScanning = false;

    protected BleScanManagerImpl(Context context) {
        super(context);
//        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
//            scanManagerImpl = new LollipopScanManager(context.getApplicationContext());
//        } else {
        scanManagerImpl = new LegacyScanManager(context.getApplicationContext());
//        }
    }

    @Override
    public void startScan(final ScanCallback callback) {
        cmdScanning = true;
        if (isScanning) {
            internalStopScan();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    internalStartScan(callback);
                }
            }, 200);
        } else {
            internalStartScan(callback);
        }
    }

    @Override
    public void stopScan(ScanCallback callback) {
        cmdScanning = false;
        internalStopScan();
    }

    @Override
    void internalStartScan(ScanCallback callback) {
        Log.w("hdr-ble", "内部启动扫描");
        if (this.bluetoothAdapter == null) {
            callback.onFail(ScanCallback.FAIL_BLE_NOT_SUPPORT);
            return;
        }
        if (!this.bluetoothAdapter.isEnabled()) {
            callback.onFail(ScanCallback.FAIL_BLE_IS_OFF);
            return;
        }
        if (!cmdScanning) {
            callback.onFail(ScanCallback.FAIL_BLE_INTERNAL_ERROR);
            return;
        }
        this.isScanning = true;
        scanManagerImpl.internalStartScan(callback);
    }

    @Override
    void internalStopScan() {
        Log.w("hdr-ble", "内部停止扫描");
        if (cmdScanning) {
            return;
        }
        this.isScanning = false;
        scanManagerImpl.internalStopScan();
    }
}
