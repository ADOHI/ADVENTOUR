package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Achievement;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlagActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String LOGTAG = "FlagActivity";

    private GoogleMap flagMap;
    private DatabaseReference appDatabase;

    @BindView(R.id.iv_flag_flag)ImageView flagImageView;
    @BindView(R.id.tv_flag_count)TextView flagCountTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();

        appDatabase = FirebaseDatabase.getInstance().getReference();


        if(intent.getExtras().getBoolean("trophy_button")) {
            Glide.with(this).load(R.drawable.trophy)
                    .thumbnail(0.1f)
                    .into(flagImageView);
            appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            double lat = 0;
                            double lng = 0;
                            User user = dataSnapshot.getValue(User.class);
                            for(Achievement a : user.getAchievementList()){
                                LatLng pinPosition = new LatLng(a.getLat(), a.getLng());
                                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.trophy_map);
                                Marker marker = flagMap.addMarker(new MarkerOptions()
                                        .title(a.getTitle())
                                        .position(pinPosition));
                                marker.setIcon(icon);
                                lat += a.getLat();
                                lng += a.getLng();
                            }
                            LatLng latLng = new LatLng(lat/user.getAchievementList().size(), lng/user.getAchievementList().size());
                            flagMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                            flagCountTextView.setText(Integer.toString(user.getAchievementList().size()));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } else {
            Glide.with(this).load(R.drawable.flag_big)
                    .thumbnail(0.1f)
                    .into(flagImageView);
            appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            double lat = 0;
                            double lng = 0;
                            User user = dataSnapshot.getValue(User.class);
                            for (Achievement a : user.getFlagList()) {
                                LatLng pinPosition = new LatLng(a.getLat(), a.getLng());
                                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.flag_small);
                                Marker marker = flagMap.addMarker(new MarkerOptions()
                                        .title(a.getTitle())
                                        .position(pinPosition));
                                marker.setIcon(icon);
                                lat += a.getLat();
                                lng += a.getLng();
                            }
                            LatLng latLng = new LatLng(lat / user.getFlagList().size(), lng / user.getFlagList().size());
                            flagMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                            flagCountTextView.setText(Integer.toString(user.getFlagList().size()));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        flagMap = googleMap;
    }
}
