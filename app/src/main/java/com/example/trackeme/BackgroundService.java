package com.example.trackeme;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.sax.EndElementListener;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Database;
import androidx.room.Room;

import com.example.trackeme.Model.DataBase;
import com.example.trackeme.Model.DatabaseDao;
import com.example.trackeme.Model.POst;
import com.example.trackeme.Model.StopPoint;
import com.example.trackeme.Model.posted_point;
import com.example.trackeme.Networking.Retrofit_Service;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class BackgroundService extends Service {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "BackgroundService";
    private MyLocationListener mLocationListener;
    private ConnectionCall connectionCall;


    private static final long INTERVAL = 1000;
    private static final long FASTEST_INTERVAL = 500;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    ArrayList<POst> pOsts;
    PostsViewModel postsViewModel;

    StopPoint mlocation;
    LifecycleOwner ActivityLifeCycle;
    FragmentActivity owner;

    Handler handler = new Handler();
    int delay = 1000*20; //milliseconds
     Runnable r =new Runnable(){
        public void run(){
            PostLocation();
            if(END.get() && CheckPointsToPost.size()==0){
                stopForeground(true);
                stopSelf();
            }
            else {
                handler.removeCallbacks(this);
                handler.postDelayed(r, delay);
            }
        }
    };

    private  OnLocationChanged callBack;
    AtomicBoolean END ;

    public static ArrayList<posted_point> CheckPointsToPost = new ArrayList<>();
    DataBase database;
    int POST_ID = 1;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private class MyLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location) {
            if(callBack!=null) {
                callBack.locationChange(location.getLongitude(), location.getLatitude());
            }
            mlocation.setLat(location.getLatitude());
            mlocation.setLong(location.getLongitude());
            Log.i(TAG, "LocationChanged: "+location);
        }
    }
    private class ConnectionCall implements  GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener{

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            startLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.i(TAG, "onCreate");
        startForeground(12345678, getNotification());
        END = new AtomicBoolean();
        END.set(false);
        Thread.setDefaultUncaughtExceptionHandler(new MyAPP(this.getApplicationContext(),owner));
    }

    @Override
    public void onDestroy()
    {

        handler.removeCallbacks(r);
        handler = null;
        r = null;
        super.onDestroy();

    }

    public void startTracking() {
        mLocationListener = new MyLocationListener();
        connectionCall = new ConnectionCall();
        Log.i(TAG, "started");
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionCall)
                .addOnConnectionFailedListener(connectionCall)
                .build();
        mGoogleApiClient.connect();

        mlocation = new StopPoint();
        pOsts = new ArrayList<>();
        database = DataBase.getInstance(this.getApplication());
        AppExcutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<posted_point> p =database.Data_Base_quaries().get();
                CheckPointsToPost.clear();
                CheckPointsToPost.addAll(p);
                if (CheckPointsToPost.size() > 0) {
                    AppExcutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            database.Data_Base_quaries().deletALL();
                        }
                    });
                }
            }
        });
        Observer<String >stringObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(!s.equals("ERROR")) {
                    for(int i =0 ;i<pOsts.get(0).getStops().size();i++){
                        CheckPointsToPost.remove(0);
                    }
                    callBack.OnPost("posted"+s);
                }
                else {
                    callBack.OnPost("Check Your Connection Mother Fucker_!_");
                }
                // Retrofit_Service.POST_res.removeObserver(this);
                pOsts.remove(0);
            }
        };
        Retrofit_Service.POST_res.observe(ActivityLifeCycle , stringObserver);
        //CheckPointsToPost.add(new StopPoint(33.2,33.5));

    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,mLocationListener);

        Log.d(TAG, "Location update started ..............: ");

    }

    public POst MakePost(){
        POst p = new POst();
        p.setID(POST_ID);
        POST_ID++;
        int loop = 10000;
        if(CheckPointsToPost.size() <=10000){
            loop=CheckPointsToPost.size();
        }
        for(int i =0 ;i<loop;i++){
            p.getStops().add(CheckPointsToPost.get(i));
        }
        return p;
    }

    public void add_point() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        //You can change "yyyyMMdd_HHmmss as per your requirement
        String currentDateandTime = simpleDateFormat.format(new Date());
        final posted_point p = new posted_point(mlocation.getLat(),mlocation.Long ,currentDateandTime );
        CheckPointsToPost.add(p);

    }

    public void PostLocation(){
        if(!END.get()){
            add_point();
        }
        if(CheckPointsToPost.size()>0){
            final POst p = MakePost();
            pOsts.add(p);
            Retrofit_Service.Post_poit(p);

        }
    }

    public void stopTracking() {
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                add_point();
            }
        });
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, mLocationListener);
        END.set(true);
    }

    public void startPosting(){
        handler.postDelayed(r, delay);
    }
    public void RegesterCallBack(OnLocationChanged callBack , LifecycleOwner lifecycleOwner,FragmentActivity o){
        this.callBack = callBack;
        this.ActivityLifeCycle = lifecycleOwner;
        this.owner = o;
    }

    private Notification getNotification() {

//        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
//
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);
//
//        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder (this, "ForegroundServiceChannel").setAutoCancel(true).build();
        return notification;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "ForegroundServiceChannel",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    public class LocationServiceBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

}
