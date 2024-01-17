package com.example.ble_test_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    enum AppPermissionRequestCodes {
        APP_PERMISSION_REQUEST_CODE_NONE,
        APP_PERMISSION_REQUEST_CODE_BLUETOOTH,
        APP_PERMISSION_REQUEST_CODE_BLUETOOTH_ADMIN,
        APP_PERMISSION_REQUEST_CODE_BLUETOOTH_PRIVILEGED,
        APP_PERMISSION_REQUEST_CODE_BLUETOOTH_SCAN,
        APP_PERMISSION_REQUEST_CODE_BLUETOOTH_CONNECT,
        APP_PERMISSION_REQUEST_CODE_BLUETOOTH_ADVERTISE,

        APP_PERMISSION_REQUEST_CODE_COURSE_LOCATION,

        APP_PERMISSION_REQUEST_CODE_FINE_LOCATION,

        APP_PERMISSION_REQUEST_CODE_BACKGROUND_LOCATION,
        APP_PERMISSION_REQUEST_CODE_MAX,

    }

    ;

    private static final String TAG = "MainActivity";


    private static final String DeviceName = "XTEND";
    private static final String DeviceMAc = "FD:90:C0:E7:82:EA";

    private ArrayList<BleDeviceItem> mDeviceItemList = new ArrayList<>();

    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothLeScanner mBleScanner = null;

    private boolean mScanningFlag = false;

    private final BluetoothGattCallback mBleGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            BluetoothDevice device=gatt.getDevice();
            Log.d(TAG, "onConnectionStateChange: { name: " + device.getName() + " , mac : "+device.getAddress());

            if(newState== BluetoothProfile.STATE_CONNECTED){
                // discover services
                Log.d(TAG, "onConnectionStateChange: Connection established { name: "
                        + device.getName() + " , mac : "+device.getAddress());
                Toast.makeText(MainActivity.this,"onConnectionStateChange: Connection established { name: "
                        + device.getName() +
                        " , mac : "+device.getAddress(),
                        Toast.LENGTH_SHORT).
                        show();
                gatt.disconnect();
            }
            else if(newState== BluetoothProfile.STATE_DISCONNECTED){
                Log.w(TAG, "onConnectionStateChange: Disconnected { name: "
                        + device.getName() + " , mac : "+device.getAddress());
                Toast.makeText(MainActivity.this,"onConnectionStateChange: Disconnected { name: "
                                        + device.getName() +
                                        " , mac : "+device.getAddress(),
                                Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }
    };

    private final ScanCallback mBleScanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, "onScanResult: Ble scan result ,callbackType : " + callbackType);
            BluetoothDevice device = result.getDevice();

            /**
             * Log and toast available ble devices.
             */
            if(IsPermissionGranted(MainActivity.this,Manifest.permission.BLUETOOTH_CONNECT)){
                String responceStr="{name : \"" + device.getName() +"\" address : "+ device.getAddress()+" }";
                Log.d(TAG, "onScanResult: Ble Device Found "+responceStr);

                // if(device.getName().equals(DeviceName) && device.getAddress().equals(DeviceMAc)){
                //     Log.d(TAG, "onScanResult: Required device available");
                //     StartStopBleScanning();

                //     BluetoothGatt server=device.connectGatt(MainActivity.this,false,mBleGattCallback);
                }

               mDeviceItemList.add(new BleDeviceItem(device.getName(),device.getAddress()));
               mRecyclerViewAdapter.notifyDataSetChanged();
            }else{
                Log.e(TAG, "onScanResult: permission not available : "+ Manifest.permission.BLUETOOTH_CONNECT);
            }


        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "onScanFailed: Ble scanning failed , errorCode: " + errorCode);
        }
    };

    /*-------UI Elements----*/
    private ExtendedFloatingActionButton mFloatingActionBtn = null;
    private RecyclerView mRecyclerView=null;
    private  RecyclerView.Adapter mRecyclerViewAdapter;
    private  RecyclerView.LayoutManager mRecyclerViewManager;

    /*---------------------*/


    private final BroadcastReceiver mBluetoothStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_TURNING_ON: {
                        Log.d(TAG, "onReceive: action: " + action + " state : BluetoothAdapter.STATE_TURNING_ON");
                        break;
                    }
                    case BluetoothAdapter.STATE_ON: {
                        Log.d(TAG, "onReceive: action: " + action + " state : BluetoothAdapter.STATE_ON");
                        break;
                    }
                    case BluetoothAdapter.STATE_TURNING_OFF: {
                        Log.d(TAG, "onReceive: action: " + action + " state : BluetoothAdapter.STATE_TURNING_OFF");
                        break;
                    }
                    case BluetoothAdapter.STATE_OFF: {
                        Log.d(TAG, "onReceive: action: " + action + " state : BluetoothAdapter.STATE_OFF");
                        break;
                    }
                    default: {
                        // do nothing
                        break;
                    }
                }
            }
        }
    };




    private boolean IsFeatureAvailable(final String featureSTring) {

        PackageManager packageManager = getPackageManager();
        if (packageManager == null) {
            Log.e(TAG, "IsFeatureAvailable: package manager instance not found");
            return false;
        }

        if (packageManager.hasSystemFeature(featureSTring)) {
            Log.d(TAG, "IsFeatureAvailable: " + featureSTring + "feature available");
            return true;
        } else {
            Log.w(TAG, "IsFeatureAvailable: " + featureSTring + "feature not available");
            return false;
        }
    }

    private boolean IsBleSupported(Context context){
            return (BluetoothAdapter.getDefaultAdapter() !=null &&
            context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE));
    }

    private boolean IsPermissionGranted(final Activity activity, final String permissionString) {
        Log.d(TAG, "IsPermissionGranted: Called");
        if (ContextCompat.checkSelfPermission(activity.getBaseContext(), permissionString) == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    private void RequestPermission(final Activity activity, final String permissionString, final int permissionRequestCode, final String promptMessage, final String permissionReason) {

        /**
         * Check weather the permission is already granted
         */
        if (IsPermissionGranted(activity, permissionString)) {
            Log.d(TAG, "RequestPermission: Permission already granted (" + permissionString + ")");
        } else {
            /**
             * If user denied permission but still try to use the app
             */
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionString)) {
                Log.d(TAG, "RequestPermission: called2");

                /**
                 * Ui to take user response.
                 */
                new AlertDialog.Builder(activity)
                        .setTitle(promptMessage)
                        .setMessage(permissionReason)
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "RequestPermission, onClick: Positive button pressed");
                                String stringArr[] = new String[]{permissionString};
                                ActivityCompat.requestPermissions(activity, stringArr, permissionRequestCode);
                            }
                        })
                        .setNegativeButton("Deni", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "RequestPermission, onClick: Negative button pressed");
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();

            } else {
                String stringArr[] = new String[]{permissionString};
                ActivityCompat.requestPermissions(activity, stringArr, permissionRequestCode);
            }
        }


    }


    private void EnableDisableBluetooth(final Activity activity, final BluetoothAdapter adapter, final BroadcastReceiver receiver) {

        if (adapter == null) {
            Log.e(TAG, "EnableDisableBluetooth: Bluetooth adapter not found");
            return;
        }

        if (adapter.isEnabled() == true) {

            /**
             * Bluetooth already enabled need to be stopped.
             */
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "EnableDisableBluetooth: No permission granted");
                return;
            }

            try {
                adapter.disable();
            } catch (Exception e) {
                Log.e(TAG, "EnableDisableBluetooth: " + e.getMessage());
            } finally {

                /**
                 * Register a Broadcast Receiver for handling bluetooth state change events
                 */
                Log.d(TAG, "EnableDisableBluetooth: Bluetooth disable request");
                IntentFilter disableBtIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(receiver, disableBtIntentFilter);
            }


        } else {
            Log.d(TAG, "EnableDisableBluetooth: Bluetooth enable request");

            /**
             * Start intent to enable Bluetooth
             */
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);

            /**
             * Register a Broadcast Receiver for handling bluetooth state change events
             */
            IntentFilter enableBtIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiver, enableBtIntentFilter);
        }

    }


    @SuppressLint("MissingPermission")
    private void StartStopBleScanning() {

        if(!IsPermissionGranted(this, Manifest.permission.BLUETOOTH_SCAN)){
            Log.e(TAG, "onCreate: Permission not granted: "+ Manifest.permission.BLUETOOTH_SCAN);
            Toast.makeText(MainActivity.this ,"required bluetooth scan permission",Toast.LENGTH_SHORT).show();
        }

        if (mScanningFlag) {
            mScanningFlag = false;

            // TODO testing
            mFloatingActionBtn.setBackgroundColor(Color.GREEN);
            mFloatingActionBtn.setText("Scan Start");

            mBleScanner.stopScan(mBleScanCallback);
            Log.d(TAG, "StartStopBleScanning: Stop Scanning");
            Toast.makeText(MainActivity.this,"Stop",Toast.LENGTH_SHORT).show();

        } else {
            mScanningFlag = true;

            // TODO testing
            mFloatingActionBtn.setBackgroundColor(Color.RED);
            mFloatingActionBtn.setText("Scan Stop");

            mBleScanner.startScan(mBleScanCallback);
            Log.d(TAG, "StartStopBleScanning: Start Scanning");
            Toast.makeText(MainActivity.this,"Start",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Called");

        /*------------Reference ui elements--------------*/
        mFloatingActionBtn = (ExtendedFloatingActionButton) findViewById(R.id.action_button);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        /*-----------------------------------------------*/

        /**
         * Check if system has ble feature
         */
        if(!IsBleSupported(MainActivity.this)){
            Log.e(TAG, "onCreate: Ble not supported");
            Toast.makeText(MainActivity.this,"System Doeesnot have ble support",Toast.LENGTH_SHORT).show();
            finish();
        }


        RequestPermission(MainActivity.this,
                Manifest.permission.BLUETOOTH_CONNECT,
                AppPermissionRequestCodes.APP_PERMISSION_REQUEST_CODE_BLUETOOTH_CONNECT.ordinal(),
                Manifest.permission.BLUETOOTH_CONNECT,
                "required for this and that");

        RequestPermission(MainActivity.this,
                Manifest.permission.BLUETOOTH_SCAN,
                AppPermissionRequestCodes.APP_PERMISSION_REQUEST_CODE_BLUETOOTH_SCAN.ordinal(),
                Manifest.permission.BLUETOOTH_SCAN,
                "required for this and that");

        RequestPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                AppPermissionRequestCodes.APP_PERMISSION_REQUEST_CODE_COURSE_LOCATION.ordinal(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
                "required for this and that");

        RequestPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                AppPermissionRequestCodes.APP_PERMISSION_REQUEST_CODE_FINE_LOCATION.ordinal(),
                Manifest.permission.ACCESS_FINE_LOCATION,
                "required for this and that");

        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        mBleScanner =mBluetoothAdapter.getBluetoothLeScanner();
        EnableDisableBluetooth(MainActivity.this,mBluetoothAdapter,mBluetoothStateChangeReceiver);


        mRecyclerView.setHasFixedSize(true);
        mRecyclerViewManager=new LinearLayoutManager(this);
        mRecyclerViewAdapter=new BleDeviceItemAdapter(mDeviceItemList);
        mRecyclerView.setLayoutManager(mRecyclerViewManager);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);



        mFloatingActionBtn.setText("Scan Start");
        mFloatingActionBtn.setBackgroundColor(Color.GREEN);
        mFloatingActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Action btn pressed");
                StartStopBleScanning();
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Called");
        super.onDestroy();
        unregisterReceiver(mBluetoothStateChangeReceiver);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Called permission: " + permissions[0] + "  requestCode: " + requestCode);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: Permission granted");
        } else {
            Log.w(TAG, "onRequestPermissionsResult: Permission denied");
        }

//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
