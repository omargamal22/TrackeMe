package com.example.trackeme;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.trackeme.Model.Response;
import com.example.trackeme.Model.StopPoint;
import com.example.trackeme.Networking.Retrofit_Service;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , OnLocationChanged {

    private GoogleMap mMap;
    public BackgroundService gpsService;
    public boolean mTracking = false;
    Marker m;
    LatLng EndP;
    ArrayList<StopPoint> stopPoints;
    int StopID;
    ArrayList<Marker> markers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Intent intent = new Intent(getApplication(), BackgroundService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//        this.getApplication().startForegroundService(intent);


    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("BackgroundService")) {
                Log.e("TrackMe","fuck");
                gpsService = ((BackgroundService.LocationServiceBinder) service).getService();
                gpsService.RegesterCallBack(MapsActivity.this ,MapsActivity.this,MapsActivity.this);
                start_tracking();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("BackgroundService")) {
                gpsService = null;
            }
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney ;
        stopPoints = Response.getInstance().stopPoints;
        EndP = new LatLng(stopPoints.get(0).Lat , stopPoints.get(0).Long);
        StopID = 0;
        markers = new ArrayList<>();
        for(int i =0 ; i < stopPoints.size();i++) {
            sydney = new LatLng(stopPoints.get(i).Lat, stopPoints.get(i).Long);
            markers.add(mMap.addMarker(new MarkerOptions().position(sydney).snippet(stopPoints.get(i).Name)));


        }
    }

    public void start_tracking(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(getApplicationContext(),"started",Toast.LENGTH_LONG).show();

                        gpsService.startTracking();


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void startLocationButtonClick(View view) {
        if(mTracking){
            Toast.makeText(this,"already Started",Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this,"Started",Toast.LENGTH_LONG).show();
        mTracking = true;
        gpsService.startPosting();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mTracking) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void stopLocationButtonClick(View view) {
        mTracking = false;
        gpsService.stopTracking();
        Toast.makeText(getApplicationContext(),"ended",Toast.LENGTH_LONG).show();
    }

    @Override
    public void locationChange(double x, double y) {

        LatLng sydney = new LatLng(y, x);
        if(m == null) {
            m = mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus)));
        }
        m.setPosition(sydney);
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney),2,null);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                sydney, 18);
        mMap.animateCamera(location);
        track_stops(sydney);
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    public void OnPost(String v) {
        Toast.makeText(this,v,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if(mTracking) {
            stopLocationButtonClick(null);
        }
        unbindService(serviceConnection);
        super.onDestroy();
    }
    public void track_stops(LatLng StartP) {
        float[] results = new float[1];
        Location.distanceBetween(StartP.latitude,StartP.longitude,EndP.latitude,EndP.longitude,results);
        if(results[0]<=500){
            LatLng x =markers.get(StopID).getPosition();
            markers.get(StopID).remove();
            markers.set(StopID , mMap.addMarker(new MarkerOptions().position(x).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
            StopID++;
            if (StopID < stopPoints.size()) {
                EndP = new LatLng(stopPoints.get(1).Lat , stopPoints.get(1).Long);
            }
        }
    }

    public void crash(View view) {
        double x = 1/0;
    }
}
