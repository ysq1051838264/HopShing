package com.hdr.blelib.scan;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hdr on 15/5/25.
 */
public class BleScannerDataItem implements Parcelable {
    public byte length;
    public byte type;
    public byte[] content;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.length);
        dest.writeByte(this.type);
        dest.writeByteArray(this.content);
    }

    public BleScannerDataItem() {
    }

    protected BleScannerDataItem(Parcel in) {
        this.length = in.readByte();
        this.type = in.readByte();
        this.content = in.createByteArray();
    }

    public static final Parcelable.Creator<BleScannerDataItem> CREATOR = new Parcelable.Creator<BleScannerDataItem>() {
        @Override
        public BleScannerDataItem createFromParcel(Parcel source) {
            return new BleScannerDataItem(source);
        }

        @Override
        public BleScannerDataItem[] newArray(int size) {
            return new BleScannerDataItem[size];
        }
    };
}
