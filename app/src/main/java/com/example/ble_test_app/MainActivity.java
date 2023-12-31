package com.example.ble_test_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    enum AppPermissionRequestCodes{
        APP_PERMISSION_REQUEST_CODE_BLUETOOTH,
        APP_PERMISSION_REQUEST_CODE_BLUETOOTH_ADMIN,
        APP_PERMISSION_REQUEST_CODE_BLUETOOTH_SCAN,
        APP_PERMISSION_REQUEST_CODE_BLUETOOTH_CONNECT,
        APP_PERMISSION_REQUEST_CODE_BLUETOOTH_ADVERTISE,
    };

    private static final String TAG="MainActivity";

    /*-------UI Elements----*/
    private Button button=null;

    private boolean IsFeatureAvailable(final String featureSTring){

        PackageManager packageManager=getPackageManager();
        if(packageManager==null){
            Log.e(TAG, "IsFeatureAvailable: package manager instance not found");
            return false;
        }

        if(packageManager.hasSystemFeature(featureSTring)){
            Log.d(TAG, "IsFeatureAvailable: "+featureSTring+ "feature available");
            return true;
        }else {
            Log.w(TAG, "IsFeatureAvailable: "+featureSTring+ "feature not available");
            return false;
        }
    }

    private void RequestPermission(final Activity activity, final String permissionString, final int permissionRequestCode, final String promptMessage, final String permissionReason){

        /**
         * Check weather the permission is already granted
         */
        if(ContextCompat.checkSelfPermission(activity.getBaseContext(),permissionString)==PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "RequestPermission: Permission already granted ("+permissionString+")");
            //TODO remove this from after testing
            Toast.makeText(activity,"Permission already granted",Toast.LENGTH_SHORT).show();

        }else{
            /**
             * If user denied permission but still try to use the app
             */
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionString)){
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
                                String stringArr[]=new String[] {permissionString};
                                ActivityCompat.requestPermissions(activity,stringArr,permissionRequestCode);
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

            }else{
                String stringArr[]=new String[] {permissionString};
                ActivityCompat.requestPermissions(activity,stringArr,permissionRequestCode);
            }
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Called");

        /*------------Reference ui elements--------------*/
        button=(Button) findViewById(R.id.button);
        /*-----------------------------------------------*/

        IsFeatureAvailable(PackageManager.FEATURE_BLUETOOTH);
        IsFeatureAvailable(PackageManager.FEATURE_BLUETOOTH_LE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestPermission(MainActivity.this,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        AppPermissionRequestCodes.APP_PERMISSION_REQUEST_CODE_BLUETOOTH.ordinal(),
                        Manifest.permission.BLUETOOTH_ADMIN,
                        "required for this and that");
            }
        });

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Called permission: "+permissions[0]+"  requestCode: "+requestCode);
        if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onRequestPermissionsResult: Permission granted");
        }else{
            Log.w(TAG, "onRequestPermissionsResult: Permission denied");
        }

//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}