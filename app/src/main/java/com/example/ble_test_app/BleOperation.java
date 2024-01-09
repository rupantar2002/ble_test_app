package com.example.ble_test_app;

import android.media.VolumeShaper;

public abstract class BleOperation {

    private String mServerId;
    public BleOperation(final String serverId){
        this.mServerId=serverId;
    }

    public String getServerId() {
        return mServerId;
    }

    public abstract void perform();
}
