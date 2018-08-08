package com.hdr.blelib.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * Created by hdr on 17/4/14.
 */

public class LegacyScanManager extends BleScanManager {
    private ScanCallback callback;

    LegacyScanManager(Context context) {
        super(context);
    }

    private final BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            final BleScannerData scannerData = new BleScannerData(scanRecord);
            if (LegacyScanManager.this.callback != null) {
                LegacyScanManager.this.callback.onScan(new ScanResult(device, scannerData, rssi));
            }
        }
    };


    @Override
    void internalStartScan(final ScanCallback callback) {
        LegacyScanManager.this.callback = callback;
        LegacyScanManager.this.bluetoothAdapter.startLeScan(LegacyScanManager.this.leScanCallback);
    }

    @Override
    void internalStopScan() {
        LegacyScanManager.this.callback = null;
        LegacyScanManager.this.bluetoothAdapter.stopLeScan(LegacyScanManager.this.leScanCallback);
    }
}
