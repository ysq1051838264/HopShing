package com.example.hopshing.ble;

import java.util.UUID;

/**
 * Created by hr on 2015/12/10.
 */
public interface DecoderAdapter {

    void onLowPower();

    void writeData(UUID serviceUUID, UUID characteristicUUID, byte[] value);

    void onBeginReceiveStorageData();

    int getMethod();

    void onUpdateDevice(String mac, String scaleName, String internalModel);

}
