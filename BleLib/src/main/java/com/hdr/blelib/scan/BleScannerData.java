package com.hdr.blelib.scan;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hdr on 15/5/25.
 */
public class BleScannerData implements Parcelable {
    public byte[] scanRecords;
    List<BleScannerDataItem> items;

    public BleScannerData(byte[] scanRecords) {
        this.scanRecords = scanRecords;
        items = new ArrayList<>();
        int index = 0;
        while (index < scanRecords.length) {
            byte length = scanRecords[index];
            if (length == 0) {
                break;
            }
            byte type = scanRecords[index + 1];
            byte[] content = new byte[length - 1];
            System.arraycopy(scanRecords, index + 2, content, 0, content.length);
            BleScannerDataItem item = new BleScannerDataItem();
            item.length = length;
            item.type = type;
            item.content = content;
            items.add(item);
            index += length + 1;
        }
    }

    public BleScannerDataItem getByType(byte type) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).type == type) {
                return items.get(i);
            }
        }
        return null;
    }

    public String getLocalName() {
        BleScannerDataItem item = getByType((byte) 0x09);
        return item == null ? null : new String(item.content);
    }

    public List<String> getServiceUuids() {
        BleScannerDataItem item = getByType((byte) 0x3);
        if (item == null || item.content == null)
            return null;
        byte[] content = item.content;
        List<String> uuids = new ArrayList<>();
        for (int i = 0; i < content.length; i += 2) {
            uuids.add(String.format("%2X%2X", content[i + 1], content[i]));
        }
        return uuids;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.scanRecords);
        dest.writeTypedList(this.items);
    }

    protected BleScannerData(Parcel in) {
        this.scanRecords = in.createByteArray();
        this.items = in.createTypedArrayList(BleScannerDataItem.CREATOR);
    }

    public static final Parcelable.Creator<BleScannerData> CREATOR = new Parcelable.Creator<BleScannerData>() {
        @Override
        public BleScannerData createFromParcel(Parcel source) {
            return new BleScannerData(source);
        }

        @Override
        public BleScannerData[] newArray(int size) {
            return new BleScannerData[size];
        }
    };
}
