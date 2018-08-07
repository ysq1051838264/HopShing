package com.example.hopshing.utils;

import java.util.UUID;

/**
 * Created by hdr on 15/5/25.
 */
public interface BleConst {

    // 体脂率相差过大的提示框
    String KEY_SHOW_BODYFAT_CHANGE_GREAT_DIALOG = "key_show_bodyfat_change_great_dialog";

    String SCAN_NAME_PREFIX = "Yolanda";
    String[] QINGNIU_BLE_NAME_LIST = new String[]{"QN-Scale", "Yolanda-CS20H", "Yolanda-CS20I", "Yolanda-CS10", "Yolanda-CS10C", "Yolanda-CS20A", "Yolanda-CS30A", "Yolanda-CS20E", "Yolanda-CS20F", "Yolanda-CS20G", "CS30C", "JiaHua-CS50A", "Dretec-CS50A", "Wbird-CS50A", "Yolanda-CS20B", "Sunnyway-CS50A", "JiaBao-CS50A", "Beryl-CS50A", "QN-WristBand", "QN-WristBand\r", "Yolanda-CS10C1", "Yolanda-CS11", "Yolanda-CS20E1", "Yolanda-CS20E2", "Yolanda-CS20F1", "Yolanda-CS20F2", "Yolanda-CS20G1", "Yolanda-CS20G2", "Beryl-CS40A"};

    UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    UUID UUID_IBT_SERVICES = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    UUID UUID_IBT_READ = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    UUID UUID_IBT_WRITE = UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb");
    // 称的名字
    UUID UUID_IBT_NAME_SERVICES = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    UUID UUID_IBT_NAME_READ = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");

    UUID UUID_IBT_BLE_WRITER = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb");
    UUID UUID_IBT_BLE_READER = UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb");
    UUID UUID_IBT_BLE_INTERNAL_MODEL = UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb");

    UUID UUID_MI_SCALE_SERVICE = UUID.fromString("0000181d-0000-1000-8000-00805f9b34fb");
    UUID UUID_MI_INDICATE_CHARACTERISTIC = UUID.fromString("00002a9d-0000-1000-8000-00805f9b34fb");
    UUID UUID_MI_NOTIFY_CHARACTERISTIC = UUID.fromString("00002a2f-0000-1000-8000-00805f9b34fb");

    //手环
    String UUID_SERVICE_SHORT_I_DO_WRIST_BAND = " AF0";

    int KEY_IS_WRIST_BAND = 2;

    byte PROTOCOL_1ST = 0x01;
    byte PROTOCOL_2ND = 0x11;
    byte PROTOCOL_3RD = 0x12;

    String SCALE_NAME_YOLANDA_COLOR = "Yolanda-CS20A";
    String SCALE_NAME_YOLANDA_WHITE = "Yolanda-CS10C";
    String SCALE_NAME_CLOUD = "Yolanda-CS10";
    String SCALE_NAME_WEIGHT = "Yolanda-CS30A";
    String SCALE_NAME_YOLANDA_20E = "Yolanda-CS20E";
    String SCALE_NAME_YOLANDA_20F = "Yolanda-CS20F";
    String SCALE_NAME_YOLANDA_20G = "Yolanda-CS20G";
    String SCALE_NAME_YOLANDA_CS11 = "Yolanda-CS11";
    String SCALE_NAME_CS30C = "CS30C";
    String SCALE_NAME_UNKNOWN = "UNKNOW";
    String SCALE_NAME_INPUT = "MANUALINPUT";

    String SCALE_NAME_QINGNIU = "QN-Scale";

    String ACTION_BLE_NEW_MEASURED_DATA = "action_ble_new_measured_data";  // 手动测量

    String ACTION_BLE_SCAN = "action_ble_scan";
    String ACTION_BLE_SCAN_BIND = "action_ble_scan_bind";

    String ACTION_BLE_CONNECTED = "action_ble_connected";
    String ACTION_BLE_CONNECTED_BIND = "action_ble_connected_bind";

    String ACTION_BLE_DISCOVERED = "action_ble_discovered";
    String ACTION_BLE_DISCOVERED_BIND = "action_ble_discovered_bind";

    String ACTION_BLE_RECEIVE_DATA = "action_ble_receive_data";
    String ACTION_BLE_RECEIVE_DATA_BIND = "action_ble_receive_data_bind";

    String ACTION_BLE_DISCONNECTED = "action_ble_disconnected";
    String ACTION_BLE_DISCONNECTED_BIND = "action_ble_disconnected_bind";


    String KEY_DEVICE_NAME = "device_name";
    String KEY_SCAN_RSSI = "scan_rssi";
    String KEY_SCAN_RECORD = "scan_record";
    String KEY_MAC = "mac";
    String KEY_UUID = "uuid";
    String KEY_DATA = "data";


    String INTERNAL_MODEL_NORMAL = "0000";
    String KEY_RECEIVED_DATA = "received_data";


}
