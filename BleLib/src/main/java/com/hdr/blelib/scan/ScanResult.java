package com.hdr.blelib.scan;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hdr on 17/4/15.
 */

public class ScanResult implements Parcelable {
    public BluetoothDevice bluetoothDevice;
    public BleScannerData scanRecord;
    public int rssi;

    public ScanResult(BluetoothDevice bluetoothDevice, BleScannerData scanRecord, int rssi) {
        this.bluetoothDevice = bluetoothDevice;
        this.scanRecord = scanRecord;
        this.rssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return bluetoothDevice;
    }

    public int getRssi() {
        return rssi;
    }

    public String getLocalName() {
        String name = this.bluetoothDevice.getName();
        if (name == null) {
            name = scanRecord.getLocalName();
        }
        return name;
    }

    public byte[] getManufacturerSpecificData() {
        return scanRecord.getByType((byte) 0xFF).content;
    }

    @Override
    public String toString() {
        return this.bluetoothDevice.getAddress() + " " + rssi + " " + this.getLocalName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.bluetoothDevice, flags);
        dest.writeParcelable(this.scanRecord, flags);
        dest.writeInt(this.rssi);
    }

    protected ScanResult(Parcel in) {
        this.bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.scanRecord = in.readParcelable(BleScannerData.class.getClassLoader());
        this.rssi = in.readInt();
    }

    public static final Parcelable.Creator<ScanResult> CREATOR = new Parcelable.Creator<ScanResult>() {
        @Override
        public ScanResult createFromParcel(Parcel source) {
            return new ScanResult(source);
        }

        @Override
        public ScanResult[] newArray(int size) {
            return new ScanResult[size];
        }
    };
}
