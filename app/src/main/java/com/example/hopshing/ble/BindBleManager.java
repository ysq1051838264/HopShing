package com.example.hopshing.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.util.Log;

import com.example.hopshing.utils.BleConst;

import java.util.LinkedList;
import java.util.Queue;

import no.nordicsemi.android.nrftoolbox.profile.BleManager;

/**
 * Created by hdr on 16/3/16.
 */
public class BindBleManager extends BleManager<BindBleManager.BindManagerCallback> {
    public BindBleManager(Context context) {
        super(context);
    }

    BluetoothGattCharacteristic nameBgc, bleBgc, deviceInfoBgc;

    private String address;

    @Override
    public BleManagerGattCallback getGattCallback() {
        return bleManagerGattCallback;
    }


    public interface BindManagerCallback extends no.nordicsemi.android.nrftoolbox.profile.BleManagerCallbacks {
        void onNameRead(String address, String name);

        void onInternalModelRead(String address, String internalModel);

    }

    private final BleManagerGattCallback bleManagerGattCallback = new BleManagerGattCallback() {

        @Override
        protected boolean isRequiredServiceSupported(BluetoothGatt gatt) {
            Log.e("绑定设备时 连接成功 绑定状态:", gatt.getDevice().getBondState()+"");
            address = gatt.getDevice().getAddress();
            int opType = getOpType(gatt);
            if (opType == BindBleService.TYPE_OP_READ_SCALE_NAME) {
                nameBgc = getCharacteristic(gatt, BleConst.UUID_IBT_NAME_SERVICES, BleConst.UUID_IBT_NAME_READ);
                return nameBgc != null;
            } else if (opType == BindBleService.TYPE_OP_READ_INTERNAL_MODEL) {
                bleBgc = getCharacteristic(gatt, BleConst.UUID_IBT_SERVICES, BleConst.UUID_IBT_BLE_READER);
                deviceInfoBgc = getCharacteristic(gatt, BleConst.UUID_IBT_SERVICES, BleConst.UUID_IBT_BLE_INTERNAL_MODEL);
                return bleBgc != null && deviceInfoBgc != null;
            } else {
                Log.e("成功","绑定设备读取");
                return false;
            }
        }

        int getOpType(BluetoothGatt gatt) {
            String deviceName = gatt.getDevice().getName();
            if (deviceName == null) {
                deviceName = "";
            }
            if (deviceName.startsWith(BleConst.SCAN_NAME_PREFIX)) {
                return BindBleService.TYPE_OP_READ_SCALE_NAME;
            } else if (deviceName.equals(BleConst.SCALE_NAME_QINGNIU)) {
                return BindBleService.TYPE_OP_READ_INTERNAL_MODEL;
            } else {
                Log.e("绑定设备读取时，错误的蓝牙对象 ", gatt.getDevice().getAddress()+ deviceName);
                return BindBleService.TYPE_OP_ERROR;
            }
        }

        @Override
        protected Queue<Request> initGatt(BluetoothGatt gatt) {
            Queue<Request> queue = new LinkedList<>();
            int opType = getOpType(gatt);
            if (opType == BindBleService.TYPE_OP_READ_SCALE_NAME) {
                queue.add(Request.newReadRequest(nameBgc));
            } else if (opType == BindBleService.TYPE_OP_READ_INTERNAL_MODEL) {
                queue.add(Request.newEnableIndicationsRequest(bleBgc));
                queue.add(Request.newWriteRequest(deviceInfoBgc, new byte[]{0x42, 0x04}));
            }
            return queue;
        }

        @Override
        protected void onDeviceDisconnected() {
            Log.e("绑定设备时 连接失败 ", address);
        }

        @Override
        protected void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (!characteristic.getUuid().equals(BleConst.UUID_IBT_NAME_READ)) {
                return;
            }
            String scaleName = new String(characteristic.getValue());
            mCallbacks.onNameRead(address, scaleName);
        }

        @Override
        protected void onCharacteristicIndicated(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (!characteristic.getUuid().equals(BleConst.UUID_IBT_BLE_READER)) {
                return;
            }
            byte[] value = characteristic.getValue();
            String internalModel = String.format("%02X%02X", value[4], value[3]);
            mCallbacks.onInternalModelRead(address, internalModel);
        }
    };

}
