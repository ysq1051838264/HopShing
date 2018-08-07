package com.hdr.blelib.scan;

/**
 * Created by hdr on 17/4/14.
 */

public interface ScanCallback {

    int FAIL_BLE_IS_OFF = 1;
    int FAIL_BLE_NOT_SUPPORT = 2;
    int FAIL_BLE_INTERNAL_ERROR = 3;

    void onScan(ScanResult scanResult);

    void onFail(int code);
}
