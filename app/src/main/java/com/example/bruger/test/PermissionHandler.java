package com.example.bruger.test;

/**
 * Created by Bruger on 23-03-2018.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class PermissionHandler extends AppCompatActivity {
    private static final String TAG = "GPS&BLE";
    private final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String BLUETOOTH_PERMISSION = Manifest.permission.BLUETOOTH_PRIVILEGED;
    private final int LOCATION_REQUEST_CODE = 1;
    private final int BLUETOOTH_REQUEST_CODE = 2;

    public void LocationCheck(Context context, Activity activity) {
        Log.i(TAG, "Location - Checking");
        CheckPermission(LOCATION_PERMISSION, context, LOCATION_REQUEST_CODE, activity);
    }

    public void BluetoothCheck(Context context, Activity activity) {
        Log.i(TAG, "Bluetooth - Checking");
        CheckPermission(BLUETOOTH_PERMISSION, context, BLUETOOTH_REQUEST_CODE, activity);
    }

    private void CheckPermission(String permission, Context context, int requestCode, Activity activity) {
        Log.i(TAG, "Permission - Checking");
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission - We Don't Have Permission");
            AskPermission(permission, requestCode, activity);
        } else {
            Log.i(TAG, "Permission - We Have Permission");
            return;
        }
        return;
    }

    private void AskPermission(String permission, int requestCode, Activity activity) {
        Log.i(TAG, "Permission - Asking for Permission");
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        Log.i(TAG, "Permission - Asked for Permission");
    }

    /* NotImplemented */
    private void ExplainPermission(int requestCode) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:

                break;
            case BLUETOOTH_REQUEST_CODE:


                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission - We now have Permission");
                } else {
                    Log.i(TAG, "Permission - We didn't get Permission");
                    ExplainPermission(requestCode);
                }
                break;
            case BLUETOOTH_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission - We now have Permission");

                } else {
                    Log.i(TAG, "Permission - We didn't get Permission");
                    ExplainPermission(requestCode);
                }
                break;
        }
    }
}