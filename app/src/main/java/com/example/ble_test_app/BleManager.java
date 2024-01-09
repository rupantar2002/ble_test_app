package com.example.ble_test_app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
public class BleManager  {

    public interface BleManagerCallback{
        public void onDeviceFound(BluetoothDevice device);
        public void onConnected(BluetoothDevice device);
        public void onDisconnected(BluetoothDevice device);

    }

    private BleManagerCallback mCallback;
    private Activity mActivity;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBleScanner;

    public BleManager(Activity activity){
        this.mActivity=activity;
    }

    public void setCallback(final BleManagerCallback callback){
        this.mCallback=callback;
    }

    public void startScanning(){

    }

    public void stopScanning(){

    }

    public void connect(BluetoothDevice device){

    }

    public void disconnect(BluetoothDevice device){

    }


}
