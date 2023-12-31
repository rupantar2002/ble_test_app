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
        APP_PERMISSION_REQUEST_CODE_MAX,

    }

    ;

    private static final String TAG = "MainActivity";

    private BluetoothAdapter gBluetoothAdapter = null;

    /*-------UI Elements----*/
    private Button gStartButton = null;
    private Button gScanButton = null;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Called");

        /*------------Reference ui elements--------------*/
        gStartButton = (Button) findViewById(R.id.button_start);
        gScanButton = (Button) findViewById(R.id.button_scan);
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

                if (gBluetoothAdapter == null) {
                    Log.e(TAG, "onClick: Bluetooth adapter not found");
                    return;
                }

                if (gBluetoothAdapter.isEnabled() == true) {
                    /**
                     * Bluetooth already enabled need to be stopped.
                     */

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "onClick: No permission granted");
                        return;
                    }

                    /**
                     * Register a Broadcast Receiver for handling bluetooth state change events
                     */
                    Log.d(TAG, "onClick: Bluetooth disable request");
                    IntentFilter disableBtIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(gStateChangeReceiver, disableBtIntentFilter);

                    gBluetoothAdapter.disable();


                } else {

                    /**
                     * Register a Broadcast Receiver for handling bluetooth state change events
                     */
                    Log.d(TAG, "onClick: Bluetooth enable request");
                    IntentFilter enableBtIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(gStateChangeReceiver, enableBtIntentFilter);

                    /**
                     * Start intent to enable Bluetooth
                     */
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);



                }
            }
        });

        gScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Scan button pressed");
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Called");
        super.onDestroy();
        unregisterReceiver(gStateChangeReceiver);
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