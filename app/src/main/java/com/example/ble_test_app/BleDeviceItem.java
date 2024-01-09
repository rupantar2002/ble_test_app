package com.example.ble_test_app;

public class BleDeviceItem {
    private String mDeviceName;
    private String mDeviceMac;


    public BleDeviceItem(String deviceName, String deviceMac) {
        this.mDeviceName = deviceName;
        this.mDeviceMac = deviceMac;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String deviceName) {
        this.mDeviceName = deviceName;
    }

    public String getDeviceMac() {
        return mDeviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.mDeviceMac = deviceMac;
    }
}
