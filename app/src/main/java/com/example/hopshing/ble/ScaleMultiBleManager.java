package com.example.hopshing.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.example.hopshing.utils.BleConst;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import no.nordicsemi.android.nrftoolbox.profile.BleManager;
import no.nordicsemi.android.nrftoolbox.profile.BleManagerCallbacks;

/**
 * Created by hdr on 16/3/17.
 */
public class ScaleMultiBleManager extends BleManager<ScaleMultiBleManager.ScaleBleCallback> {
    BluetoothGattCharacteristic yolandaReadBgc, yolandaWriteBgc, yolandaBleReadBgc, yolandaBleWriteBgc, yolandaNameReadBgc, yolandaBleInfoWriterBgc, miNotifyBgc, miIndicateBgc;

    public ScaleMultiBleManager(Context context) {
        super(context);
    }

    @Override
    public BleManagerGattCallback getGattCallback() {
        return bleManagerGattCallback;
    }

    public interface ScaleBleCallback extends BleManagerCallbacks {
        void onReceiveData(UUID uuid, byte[] value);

        int getProtocolType();

        String getCurrentAddress();

        boolean needReadScaleName();

        boolean needReadInternalModel();

    }

    public void writeData(UUID serviceUUID, UUID characteristicUUID, byte[] value) {
        BluetoothGattCharacteristic bgc = null;
        if (yolandaWriteBgc != null && characteristicUUID.equals(yolandaWriteBgc.getUuid())) {
            bgc = yolandaWriteBgc;
        } else if (yolandaBleWriteBgc != null && characteristicUUID.equals(yolandaBleWriteBgc.getUuid())) {
            bgc = yolandaBleWriteBgc;
        } else if (yolandaBleInfoWriterBgc != null && characteristicUUID.equals(yolandaBleInfoWriterBgc.getUuid())) {
            bgc = yolandaBleInfoWriterBgc;
        } else {
            return;
        }
        bgc.setValue(value);
        Log.e("蓝牙日志：","发送数据:"+ byteArrayToString(value));
        writeCharacteristic(bgc);
    }

    String byteArrayToString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public BluetoothGattCharacteristic getCharacteristic(final BluetoothGatt gatt, UUID serviceUuid, UUID characteristicUuid) {
        BluetoothGattService service = gatt.getService(serviceUuid);
        if (service == null) {
            return null;
        }
        return service.getCharacteristic(characteristicUuid);
    }

    final BleManagerGattCallback bleManagerGattCallback = new BleManagerGattCallback() {
        @Override
        protected boolean isRequiredServiceSupported(BluetoothGatt gatt) {
            String address = gatt.getDevice().getAddress();
            if (!address.equals(mCallbacks.getCurrentAddress())) {
                Log.e("蓝牙日志：","地址不匹配，断开连接");
                return false;
            }
            switch (mCallbacks.getProtocolType()) {
                case 6: {
                    //小米设备
                    miNotifyBgc = getCharacteristic(gatt, BleConst.UUID_MI_SCALE_SERVICE, BleConst.UUID_MI_NOTIFY_CHARACTERISTIC);
                    miIndicateBgc = getCharacteristic(gatt, BleConst.UUID_MI_SCALE_SERVICE, BleConst.UUID_MI_INDICATE_CHARACTERISTIC);
                    return miNotifyBgc != null && miIndicateBgc != null;
                }

                case 3: {
                    //体重秤
                    yolandaReadBgc = getCharacteristic(gatt, BleConst.UUID_IBT_SERVICES, BleConst.UUID_IBT_READ);
                    return yolandaReadBgc != null;
                }
                case -1: {
                    return false;
                }
                default: {
                    //普通智能秤
                    yolandaReadBgc = getCharacteristic(gatt, BleConst.UUID_IBT_SERVICES, BleConst.UUID_IBT_READ);
                    yolandaWriteBgc = getCharacteristic(gatt, BleConst.UUID_IBT_SERVICES, BleConst.UUID_IBT_WRITE);
                    yolandaBleReadBgc = getCharacteristic(gatt, BleConst.UUID_IBT_SERVICES, BleConst.UUID_IBT_BLE_READER);
                    yolandaBleWriteBgc = getCharacteristic(gatt, BleConst.UUID_IBT_SERVICES, BleConst.UUID_IBT_BLE_WRITER);
                    yolandaNameReadBgc = getCharacteristic(gatt, BleConst.UUID_IBT_NAME_SERVICES, BleConst.UUID_IBT_NAME_READ);
                    yolandaBleInfoWriterBgc = getCharacteristic(gatt, BleConst.UUID_IBT_SERVICES, BleConst.UUID_IBT_BLE_INTERNAL_MODEL);
                    LogUtils.saveBleLog("初始化特征值，",yolandaReadBgc != null ,yolandaWriteBgc != null);
                    return yolandaReadBgc != null && yolandaWriteBgc != null;
                }

            }
        }


        @Override
        protected void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            mCallbacks.onReceiveData(characteristic.getUuid(), characteristic.getValue());
        }

        @Override
        protected void onCharacteristicNotified(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            mCallbacks.onReceiveData(characteristic.getUuid(), characteristic.getValue());
        }

        @Override
        protected void onCharacteristicIndicated(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            mCallbacks.onReceiveData(characteristic.getUuid(), characteristic.getValue());
        }

        @Override
        protected Queue<Request> initGatt(BluetoothGatt gatt) {
            Queue<Request> queue = new LinkedList<>();
            switch (mCallbacks.getProtocolType()) {
                case 6: {
                    //小米设备
                    queue.add(Request.newEnableNotificationsRequest(miNotifyBgc));
                    queue.add(Request.newEnableIndicationsRequest(miIndicateBgc));
                    break;
                }
                case 3: {
                    //体重秤
                    queue.add(Request.newEnableNotificationsRequest(yolandaReadBgc));
                    break;
                }
                case -1: {
                    break;
                }
                default: {
                    //普通智能秤
                    queue.add(Request.newEnableNotificationsRequest(yolandaReadBgc));
                    if (yolandaBleReadBgc != null) {
                        queue.add(Request.newEnableIndicationsRequest(yolandaBleReadBgc));
                    }
                    if (mCallbacks.needReadScaleName() && yolandaNameReadBgc != null) {
                        queue.add(Request.newReadRequest(yolandaNameReadBgc));
                    } else if (mCallbacks.needReadInternalModel() && yolandaBleInfoWriterBgc != null) {
                        queue.add(Request.newWriteRequest(yolandaBleInfoWriterBgc, new byte[]{0x42, 0x04}));
                    }
                }
            }
            return queue;
        }

        @Override
        protected void onDeviceDisconnected() {
            Log.e("蓝牙日志：","断开了蓝牙连接");
            yolandaReadBgc = null;
            yolandaWriteBgc = null;
            yolandaBleReadBgc = null;
            yolandaBleWriteBgc = null;
            yolandaNameReadBgc = null;
            yolandaBleInfoWriterBgc = null;
            miNotifyBgc = null;
            miIndicateBgc = null;
        }
    };
}
