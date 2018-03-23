package com.example.bruger.test;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Bruger on 19-03-2018.
 */
public class GPSManager extends Service implements LocationListener {

    //PermissionHandler PMH = new PermissionHandler();
    private static final String TAG = "GPS&BLE";

    private Context context;
    private Activity activity;
    private Boolean isGPSEnabled;
    private LocationManager locationManager;

    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude
    private float Precision; // precision of scanning

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1; // 1 sec

    public  GPSManager (Context _context, Activity _activity){
        context = _context;
        activity =_activity;
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        GPSEnabled();

    }

    public double getLongitude(){
        Log.i(TAG, "getLongitude - Getting Location");
        GetLocation();
        Log.i(TAG, "getLongitude - Got Location");
        longitude = location.getLongitude();
        Log.i(TAG, "getLongitude - My Longitude is" + longitude);
        return  longitude;
    }

    public double getLatitude(){
        Log.i(TAG, "getLatitude - Getting Location");
        GetLocation();
        Log.i(TAG, "getLatitude - Got Location");
        latitude = location.getLatitude();
        Log.i(TAG, "getLongitude - My latitude is" + latitude);
        return latitude;
    }

    public float getPrecision(){
        Log.i(TAG, "getPrecision - Getting Location");
        GetLocation();
        Log.i(TAG, "getPrecision - Got Location");
        Precision = location.getAccuracy();
        Log.i(TAG, "getLongitude - My Precision is" + Precision);
        return Precision;
    }
    /* NotImplemented */
    private void GPSEnabled(){
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled){
            // the GPS is turned On
            return;
        }else{
            // the GPS is turned Off

        }
    }

    @SuppressWarnings({"ResourceType"})
    private void GetLocation(){
        //PMH.LocationCheck(context, activity);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

