package com.adostudio.adohi.adventour.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.adostudio.adohi.adventour.GetAchivementActivity;
import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.User;
import com.adostudio.adohi.adventour.db.Achievement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.maplib.NGeoPoint;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ADOHI on 2017-02-16.
 */

public class LocationService extends Service{

    public LocationService(){}

    private static final String LOGTAG = "LocationService";
    private static final int ACHIEVEMENT_GET_DISTANCE = 3000;
    private static NMapLocationManager mapLocationManager;
    private DatabaseReference appDatabase;
    private String uid;

    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, final NGeoPoint myLocation) {
            try {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                } else {
                    Log.d(LOGTAG, "user unsigned");
                }

                MyApplication.setCurrentLng(myLocation.getLongitude());
                MyApplication.setCurrentLat(myLocation.getLatitude());
                appDatabase.child("users").child(uid).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        User user = mutableData.getValue(User.class);
                        int index = 0;
                        Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
                        currentLocation.setLongitude(myLocation.getLongitude());
                        currentLocation.setLatitude(myLocation.getLatitude());

                        for (Achievement a : user.getBookmarkList()) {
                            Location bookmarkLocation = new Location(LocationManager.GPS_PROVIDER);
                            bookmarkLocation.setLongitude(a.getLng());
                            bookmarkLocation.setLatitude(a.getLat());
                            int distance = (int) currentLocation.distanceTo(bookmarkLocation);
                            Log.d("distance", "" + distance);
                            Date date = new Date(System.currentTimeMillis());
                            SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                            String currentDate = dateFormat.format(date);

                            if (distance < ACHIEVEMENT_GET_DISTANCE) {
                                Achievement achievement = a;
                                achievement.setTime(currentDate);
                                achievement.setDistance(distance);
                                user.getAchievementList().add(0, achievement);
                                user.removeBookmark(a.getContentId());
                                Intent intent = new Intent(getApplicationContext(), GetAchivementActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("imageurl", achievement.getImageUrl());
                                intent.putExtra("title", achievement.getTitle());
                                intent.putExtra("contentid", achievement.getContentId());
                                startActivity(intent);
                            }
                            else {
                                a.setDistance(distance);
                            }

                        }

                        for (Achievement a : user.getAchievementList()) {
                            Location achievementLocation = new Location(LocationManager.GPS_PROVIDER);
                            achievementLocation.setLongitude(a.getLng());
                            achievementLocation.setLatitude(a.getLat());
                            int distance = (int) currentLocation.distanceTo(achievementLocation);
                            a.setDistance(distance);
                        }
                        appDatabase.child("users").child(uid).setValue(user);
                        return Transaction.success(mutableData);

                    }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                  DataSnapshot dataSnapshot) {
                                  Log.d(LOGTAG, "update & getting achievement success");
                            }
                      });

            }catch (Exception ex) {
                Log.d(LOGTAG, "checking distance falied");
            }

            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
            Log.d(LOGTAG, "location update timeout");
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
            Log.d(LOGTAG, "location unavailableArea");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        appDatabase = FirebaseDatabase.getInstance().getReference();

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
