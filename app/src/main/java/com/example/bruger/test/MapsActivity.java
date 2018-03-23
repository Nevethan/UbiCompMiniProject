package com.example.bruger.test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.util.IslamicCalendar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "Client";

    String[] permissions = {
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_PRIVILEGED};


    Button btn_GetGPSLocation, btn_GetBLELocation, btn_GetCombineLocation;
    TextView TV_GPS, TV_BLE;
    ListView listview;

    Context mainContext = this;
    Activity mainActivity = this;

    GPSManager gpsManager;

    private GoogleMap mMap;

    private ProximityManager proximityManager;

    private Marker markerGPS, markerBLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Kontakt SDK for BLE
        KontaktSDK.initialize("HuCksZrCxgByqVhQUIoIZzDismzpafEe");

        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setIBeaconListener(createIBeaconListener());

        listview = (ListView) findViewById(R.id.listview);

        btn_GetGPSLocation = findViewById(R.id.btn_GetGPSLocation);

        TV_GPS = findViewById(R.id.TV_GPS);
        TV_BLE = findViewById(R.id.TV_BLE);

        btn_GetGPSLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "GPS - Button Pressed");
                gpsManager = new GPSManager(mainContext, mainActivity);

                //PMH.LocationCheck(mainContext, mainActivity);
                TV_GPS.setText("(" + String.valueOf(gpsManager.getLatitude()) + ", " + String.valueOf(gpsManager.getLongitude()) + ")" + "\n"
                + "Precision of GPS " + " " + String.valueOf(gpsManager.getPrecision()));

                if(markerGPS != null){
                    markerGPS.remove();
                }

                LatLng gpsLocation = new LatLng(gpsManager.getLatitude(), gpsManager.getLongitude());
                markerGPS = mMap.addMarker(new MarkerOptions().position(gpsLocation).title("GPS Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gpsLocation,13));

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera,
        LatLng sdu = new LatLng(55.367652, 10.419619);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sdu,5));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!hasPermissions(this, permissions)) {
            requestPermissions();
        } else {
            Toast.makeText(this, "All Permissions granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        proximityManager.stopScanning();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        proximityManager.disconnect();
        proximityManager = null;
        super.onDestroy();
    }

    public void startScanning(View view) {
        Toast.makeText(this, "Begin Scanning", Toast.LENGTH_SHORT).show();
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });

    }

    List<IBeaconDevice> blist;
    //ArrayAdapter<String> adapter;

    private IBeaconListener createIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> iBeacons, IBeaconRegion region) {
                for(int i = 0; i<iBeacons.size(); i++){
                    Log.i("Sample", "IBeacons discovered" + iBeacons.get(i).getUniqueId() + " " + iBeacons.get(i).getDistance());
                }
                Log.i("Sample", "IBeacons discovered" + iBeacons.toString());
                print(iBeacons);

                calculateCentroid();
            }
        };
    }

    ArrayList<BLECoordinate> info = new ArrayList<>();
    private void getBLECoordinates(){
        info.add(new BLECoordinate("QP9Y", 55.3674408, 10.4306835));
        info.add(new BLECoordinate("S5XN", 55.3675119, 10.4306739));
        info.add(new BLECoordinate("27wH", 55.3672236, 10.4307125));
        info.add(new BLECoordinate("69Rq", 55.3671361, 10.4307242));
        info.add(new BLECoordinate("KdtM", 55.36714160443549, 10.431011701002717));
        info.add(new BLECoordinate("krQd", 55.3672252, 10.4310002));
        info.add(new BLECoordinate("LxPa", 55.3674046, 10.4309762));
        info.add(new BLECoordinate("Y2bm", 55.3674598, 10.4309688));
        info.add(new BLECoordinate("h6YJ", 55.3675242, 10.4309602));
        info.add(new BLECoordinate("5f4E", 55.3670288, 10.4307386));
        info.add(new BLECoordinate("3rMl", 55.3676085, 10.430661));
        info.add(new BLECoordinate("F29K", 55.3676208, 10.4309472));

    }

    ArrayList<BLECoordinate> results = new ArrayList<>();
    private void print(List<IBeaconDevice> list){
        //Get the coordinates for BLE Beacons - The coordinates are for the rooms that contains the beacons
        ArrayList<String> displayInfo = new ArrayList<>();
        getBLECoordinates();

        for(int i = 0; i < list.size(); i++){
            String unique = list.get(i).getUniqueId();

            for(int j = 0; j < info.size(); j++){
                if(info.get(j).getName().equals(unique)){
                    double lat = info.get(j).getLat();
                    double lng = info.get(j).getLng();
                    double distance = list.get(i).getDistance();

                    results.add(new BLECoordinate(lat,lng,distance));

                    displayInfo.add(unique);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, displayInfo);
        listview.setAdapter(adapter);
    }

    private double latPos, lngPos;
    private void calculateCentroid(){
        Collections.sort(results, new Comparator<BLECoordinate>() {
            @Override
            public int compare(BLECoordinate bleCoordinate, BLECoordinate t1) {
                if(bleCoordinate.getDistance() < t1.getDistance()){
                    return 1;
                }else{
                    return 0;
                }
            }
        });

        if(results.size() > 1){
            for(int i = 0; i < results.size();i++){
                latPos =+ results.get(i).getLat() * avgDistanceLim(i);
                lngPos =+ results.get(i).getLng() * avgDistanceLim(i);
            }
            latPos = latPos / 2 * avgDistance();
            lngPos = lngPos / 2 * avgDistance();

        }else{
            latPos = results.get(0).getLat();
            lngPos = results.get(0).getLng();
        }

        /*if(results.size() > 2){
            xPos = ((results.get(0).getLat() * ((1/results.get(1).getDistance())/(1/results.get(2).getDistance()))
                    + (results.get(1).getLat() * ((1/results.get(2).getDistance())/(1/results.get(0).getDistance()))
                    + (results.get(2).getLat() * ((1/results.get(0).getDistance())/(1/results.get(1).getDistance()))))))
                    / 2 * ((1/results.get(0).getDistance()) + (1/results.get(1).getDistance()) + (1/results.get(2).getDistance()));

            yPos = (results.get(0).getLng() * ((1/results.get(1).getDistance())/(1/results.get(2).getDistance()))
                    + (results.get(1).getLng() * ((1/results.get(2).getDistance())/(1/results.get(0).getDistance()))
                    + (results.get(2).getLng() * ((1/results.get(0).getDistance())/(1/results.get(1).getDistance())))))
                    / 2 * ((1/results.get(0).getDistance()) + (1/results.get(1).getDistance()) + (1/results.get(2).getDistance()));

        }*/

        TV_BLE.setText("Test");
        //TV_BLE.setText("(" + String.valueOf(latPos) + ", " + String.valueOf(lngPos) + ")");

        if(markerBLE != null){
            markerBLE.remove();
        }

        LatLng gpsLocation = new LatLng(latPos, lngPos);
        markerBLE = mMap.addMarker(new MarkerOptions().position(gpsLocation).title("BLE Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gpsLocation,13));


    }

    private double avgDistanceLim(int subtract) {
        double avg = 0;
        for (int i =0; i<results.size();i++) {
            avg =+ 1 / results.get(i).getDistance();
        }
        avg = avg - (1 / results.get(subtract).getDistance());
        return avg;
    }

    private double avgDistance() {
        double avg = 0;
        for (int i =0; i<results.size();i++) {
            avg += 1 / results.get(i).getDistance();
        }
        return avg;
    }

    private boolean hasPermissions(Context context, String[] perms){
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println(permission);
                    return false;
                }
            }
        }
        System.out.println("All granted");
        return true;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,permissions, 1);

    }
}
