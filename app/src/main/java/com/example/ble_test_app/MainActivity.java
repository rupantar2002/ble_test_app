package com.example.ble_test_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.security.PrivateKey;
import java.security.PrivilegedAction;

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
//    private void RequestPermission(){
//
//    }

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
                Toast.makeText(MainActivity.this,"Clicked",Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }




}