package com.example.ble_test_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


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

    private BluetoothAdapter gBluetoothAdapter = null;

    /*-------UI Elements----*/
    private Button gStartButton = null;
    private Button gScanButton = null;
    private Button gDiscoveryButton = null;

    /*---------------------*/

    private final BroadcastReceiver gStateChangeReceiver = new BroadcastReceiver() {
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

    private final BroadcastReceiver gScanModeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE: {
                        Log.d(TAG, "onReceive: action: " + action + " state : BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE");
                        break;
                    }
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE: {
                        Log.d(TAG, "onReceive: action: " + action + " state : BluetoothAdapter.SCAN_MODE_CONNECTABLE");
                        break;
                    }
                    case BluetoothAdapter.STATE_CONNECTING: {
                        Log.d(TAG, "onReceive: action: " + action + " state : BluetoothAdapter.STATE_CONNECTING");
                        break;
                    }
                    case BluetoothAdapter.STATE_CONNECTED: {
                        Log.d(TAG, "onReceive: action: " + action + " state : BluetoothAdapter.STATE_CONNECTED");
                        break;
                    }
                    case BluetoothAdapter.STATE_DISCONNECTING: {
                        Log.d(TAG, "onReceive: action: " + action + " state : BluetoothAdapter.STATE_DISCONNECTING");
                        break;
                    }
                    case BluetoothAdapter.STATE_DISCONNECTED: {
                        Log.d(TAG, "onReceive: action: " + action + " state : BluetoothAdapter.STATE_DISCONNECTED");
                        break;
                    }
                    case BluetoothAdapter.SCAN_MODE_NONE: {
                        Log.d(TAG, "onReceive: action: " + action + " state : BluetoothAdapter.SCAN_MODE_NONE");
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

    private final BroadcastReceiver gDeviceFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // TODO do something after getting device data

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "onReceive: permission not  granted i.e" + Manifest.permission.BLUETOOTH_CONNECT);
                    return;
                }
                Log.d(TAG, "onReceive: Device Found { device_name : " + device.getName() + " MAC : " + device.getAddress() + " }");
                Toast.makeText(MainActivity.this,"{Device Found : device_name : " + device.getName() + " MAC : " + device.getAddress() + " }",Toast.LENGTH_SHORT).show();
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
            //TODO remove this from after testing
            Toast.makeText(activity, "Permission already granted", Toast.LENGTH_SHORT).show();

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

    private void EnableDisableBluetoothDiscovery(final Activity activity, final BluetoothAdapter adapter, final BroadcastReceiver receiver) {

        if (adapter.isEnabled() == false) {
            Log.e(TAG, "EnableDisableBluetoothDiscovery: Bluetooth is disabled");
            Toast.makeText(activity, "Please enable bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent enableBtDiscoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enableBtDiscoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "EnableDisableBluetoothDiscovery: Permission not granted "+ Manifest.permission.BLUETOOTH_ADVERTISE);
            Toast.makeText(activity, "Enable advertise permission", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(enableBtDiscoveryIntent);

        IntentFilter enableBtIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(receiver, enableBtIntentFilter);
    }

    private void DiscoverDevices(final Activity activity, final BluetoothAdapter adapter, final BroadcastReceiver receiver) {

        if (adapter.isEnabled() == false) {
            Log.e(TAG, "DiscoverDevices: Bluetooth is disabled");
            Toast.makeText(activity, "Please enable bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "DiscoverDevices: Permission not granted "+Manifest.permission.BLUETOOTH_SCAN);
            Toast.makeText(activity, "Enable Scanning permission", Toast.LENGTH_SHORT).show();
            return;
        }

        if (adapter.isDiscovering() == true) {
            // TODO check for exceptions
            adapter.cancelDiscovery();
            adapter.startDiscovery();
            IntentFilter startDiscoveryFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(gDeviceFoundReceiver,startDiscoveryFilter);
            Log.d(TAG, "DiscoverDevices: start discovery 1");


        }else{
            // TODO check for exceptions
            adapter.startDiscovery();
            IntentFilter startDiscoveryFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(gDeviceFoundReceiver,startDiscoveryFilter);
            Log.d(TAG, "DiscoverDevices: start discovery 2");
        }

        Log.d(TAG, "DiscoverDevices: Looking for devices");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Called");

        /*------------Reference ui elements--------------*/
        gStartButton = (Button) findViewById(R.id.button_start);
        gScanButton = (Button) findViewById(R.id.button_scan);
        gDiscoveryButton = (Button) findViewById(R.id.button_discover);
        /*-----------------------------------------------*/

        /**
         * Check weather bluetooth feature is available.
         */
        IsFeatureAvailable(PackageManager.FEATURE_BLUETOOTH);

        /**
         * Check weather bluetooth low energy feature is available.
         */
        IsFeatureAvailable(PackageManager.FEATURE_BLUETOOTH_LE);

        RequestPermission(MainActivity.this,
                Manifest.permission.BLUETOOTH_CONNECT,
                AppPermissionRequestCodes.APP_PERMISSION_REQUEST_CODE_BLUETOOTH_CONNECT.ordinal(),
                Manifest.permission.BLUETOOTH_CONNECT,
                "required for this and that");

        RequestPermission(MainActivity.this,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                AppPermissionRequestCodes.APP_PERMISSION_REQUEST_CODE_BLUETOOTH_ADVERTISE.ordinal(),
                Manifest.permission.BLUETOOTH_ADVERTISE,
                "required for this and that");




        /**
         * Request bluetooth permission.
         */
//        RequestPermission(MainActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                AppPermissionRequestCodes.APP_PERMISSION_REQUEST_CODE_FINE_LOCATION.ordinal(),
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                "required for this and that");

        gBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        gStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Start button pressed");
                EnableDisableBluetooth(MainActivity.this,gBluetoothAdapter,gStateChangeReceiver);
            }
        });

        gScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Scan button pressed");
                EnableDisableBluetoothDiscovery(MainActivity.this,gBluetoothAdapter,gScanModeChangeReceiver);
            }
        });

        gDiscoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Discovery button pressed");

                RequestPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        AppPermissionRequestCodes.APP_PERMISSION_REQUEST_CODE_FINE_LOCATION.ordinal(),
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        "required for this and that");

                RequestPermission(MainActivity.this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        AppPermissionRequestCodes.APP_PERMISSION_REQUEST_CODE_BACKGROUND_LOCATION.ordinal(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        "required for this and that");

                DiscoverDevices(MainActivity.this,gBluetoothAdapter,gDeviceFoundReceiver);

            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Called");
        super.onDestroy();
        unregisterReceiver(gStateChangeReceiver);
        unregisterReceiver(gScanModeChangeReceiver);
        unregisterReceiver(gDeviceFoundReceiver);
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