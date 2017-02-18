package com.example.adohi.adventour.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.adohi.adventour.GetAchivementActivity;
import com.example.adohi.adventour.db.Achievement;
import com.example.adohi.adventour.db.Bookmark;
import com.example.adohi.adventour.db.User;
import com.example.adohi.adventour.db.achieve;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.maplib.NGeoPoint;

/**
 * Created by ADOHI on 2017-02-16.
 */

public class LocationService extends Service {
    public LocationService(){
        }
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    public static NMapLocationManager mapLocationManager;
    private DatabaseReference mDatabase;
    private String uid;
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, final NGeoPoint myLocation) {
            try {
                mDatabase.child("users").child(uid).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        User user = mutableData.getValue(User.class);
                        int index = 0;
                        Location location1 = new Location(LocationManager.GPS_PROVIDER);
                        location1.setLongitude(myLocation.getLongitude());
                        location1.setLatitude(myLocation.getLatitude());

                        for (achieve a : user.bookmarkList) {
                            Location location2 = new Location(LocationManager.GPS_PROVIDER);
                            location2.setLongitude(a.lng);
                            location2.setLatitude(a.lat);
                            int distance = (int) location1.distanceTo(location2);
                            Log.d("distance", "" + distance);
                            achieve achieve = new achieve(a.contentId, a.contentTypeId, a.overview, a.title, a.address,
                                    a.phonecall, a.homepage, System.currentTimeMillis(), a.lng, a.lat, a.imageUrl, a.distance);
                            if (distance < 10) {
                                user.achievementList.add(achieve);
                                user.removeBookmark(a.contentId);
                                Intent intent = new Intent(getApplicationContext(), GetAchivementActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("imageurl", achieve.imageUrl);
                                intent.putExtra("title", achieve.title);
                                startActivity(intent);
                            }
                            else {
                                a.distance = distance;
                            }
                            mDatabase.child("users").child(uid).child("bookmarkList").setValue(user.bookmarkList);
                            mDatabase.child("users").child(uid).child("achievementList").setValue(user.achievementList);

                        }

                        return Transaction.success(mutableData);

                    }


                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                  DataSnapshot dataSnapshot) {
                              // Transaction complete
                            }
                      });

            }catch (Exception ex) {

            }



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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            uid = user.getUid();

        } else {
            // No user is signed in
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        return super.onStartCommand(intent, flags, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
