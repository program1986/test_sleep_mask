package com.shaungyu.eyes;

import android.bluetooth.BluetoothClass;
import android.os.Parcel;
import android.os.Parcelable;

public class BLEDeviceBean implements Parcelable {
    private String name;
    private String address;
    private String type;//蓝牙类型：classic、ble、dual.....
    private int deviceType = BluetoothClass.Device.Major.UNCATEGORIZED;//蓝牙设备类型：phone、computer、vedio、wear....
    private int deviceResourceId = -1;//设备资源id
    private int rssi;//蓝牙设备信号



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceResourceId() {
        return deviceResourceId;
    }

    public void setDeviceResourceId(int deviceResourceId) {
        this.deviceResourceId = deviceResourceId;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.type);
        dest.writeInt(this.deviceType);
        dest.writeInt(this.deviceResourceId);
        dest.writeInt(this.rssi);
    }

    public BLEDeviceBean() {
    }

    protected BLEDeviceBean(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        this.type = in.readString();
        this.deviceType = in.readInt();
        this.deviceResourceId = in.readInt();
        this.rssi = in.readInt();
    }

    public static final Creator<BLEDeviceBean> CREATOR = new Creator<BLEDeviceBean>() {
        @Override
        public BLEDeviceBean createFromParcel(Parcel source) {
            return new BLEDeviceBean(source);
        }

        @Override
        public BLEDeviceBean[] newArray(int size) {
            return new BLEDeviceBean[size];
        }
    };
}
