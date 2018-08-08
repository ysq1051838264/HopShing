package com.hansion.h_ble.callback;

/**
 * Created by yangshuquan on 2018/8/4.
 */
public interface ConnectCallback {
    /**
     * Notify之后的回调
     */
    void onConnSuccess();

    void onConnFailed();

}
