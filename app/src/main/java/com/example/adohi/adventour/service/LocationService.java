package com.example.adohi.adventour.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.adohi.adventour.MainActivity;
import com.example.adohi.adventour.db.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.maplib.NGeoPoint;

/**
 * Created by ADOHI on 2017-02-16.
 */

public class LocationService extends Service {
    public LocationService(){
        }
    public static int aaa;
    public static NMapLocationManager mapLocationManager;
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
            Log.d("whyyyy", "되나?");
            Intent intent = new Intent();
            intent.setAction("loaction");
            intent.putExtra("x", myLocation.getLongitude());
            intent.putExtra("y", myLocation.getLatitude());
            sendBroadcast(intent);
            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

            // stop location updating
            //			Runnable runnable = new Runnable() {
            //				public void run() {
            //					stopMyLocation();
            //				}
            //			};
            //			runnable.run();

        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

        }

    };

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mapLocationManager = new NMapLocationManager(this);
        mapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
        mapLocationManager.enableMyLocation(false);
        return super.onStartCommand(intent, flags, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
