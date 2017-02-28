package com.adostudio.adohi.adventour;


import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.adostudio.adohi.adventour.db.User;
import com.adostudio.adohi.adventour.db.Achievement;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
        ,TabLayout.OnTabSelectedListener{

    private static final String LOGTAG = "MapsActivity";
    private static final int NEAR_SEARCH_DISTANCE = 1000;
    private GoogleMap achievementMap;
    private SupportMapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private HashMap<Marker, Integer> markerHashMap;
    private Location lastLocation;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private PendingResult<LocationSettingsResult> result;
    final int PLACE_PICKER_REQUEST = -1;
    private ArrayList<Achievement> markerAchievementList;
    private ArrayList<Achievement> markerBookmarkList;
    private ArrayList<Achievement> trophyList;
    @BindView(R.id.bt_tracker)FloatingActionButton trackerFloatingButton;
    @OnClick(R.id.bt_tracker)void trackerButtonClick(){
        LatLng target;
        try {
            target = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        } catch (Exception ex){
            target = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.zoom(15);
        builder.target(target);
        this.achievementMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
    }
    @BindView(R.id.et_searchwindow)EditText searchWindowEditText;
    @BindView(R.id.bt_search)FloatingActionButton searchFloatingButton;
    @OnClick(R.id.bt_search)void searchButtonClick()  {
        try {
            StringBuilder urlBuilder = new StringBuilder(getString(R.string.tour_keyword_search_first));
            urlBuilder.append(getString(R.string.tour_api_key));
            urlBuilder.append(getString(R.string.tour_keyword_search_second));
            urlBuilder.append(URLEncoder.encode(searchWindowEditText.getText().toString(), "utf-8"));
            urlBuilder.append(getString(R.string.tour_keyword_search_third));
            GetXMLTask task = new GetXMLTask();
            task.execute(urlBuilder.toString());
        }catch (Exception ex){}
            Log.d(LOGTAG, "parsing failed");
    }

    @BindView(R.id.rv_achievement)RecyclerView achievementRecyclerView;
    @BindView(R.id.rv_bookmark_achievement)RecyclerView bookmarkRecyclerView;
    @BindView(R.id.rv_trophy_achievement)RecyclerView trophyRecyclerView;

    private Document parsingDocument = null;


    private RecyclerView.Adapter achievementAdapter;
    private RecyclerView.Adapter bookmarkAdapter;
    private RecyclerView.Adapter trophyAdapter;

    private RecyclerView.LayoutManager achievementLayoutManager;
    private RecyclerView.LayoutManager bookmarkLayoutManager;
    private RecyclerView.LayoutManager trophyLayoutManager;


    private DatabaseReference appDatabase;

    private TabLayout achievementTabLayout;



    private RequestManager glideRequestManager;
    @BindView(R.id.bt_refresh)FloatingActionButton refreshFloatingButton;
    @OnClick(R.id.bt_refresh)void refreshButtonClick(){
        try {
            StringBuilder urlBuilder = new StringBuilder(getString(R.string.tour_location_search_first));
            urlBuilder.append(getString(R.string.tour_api_key));
            urlBuilder.append(getString(R.string.tour_location_search_second));
            urlBuilder.append(String.format("%.6f", achievementMap.getCameraPosition().target.longitude));
            urlBuilder.append(getString(R.string.tour_location_search_third));
            urlBuilder.append(String.format("%.6f", achievementMap.getCameraPosition().target.latitude));
            urlBuilder.append(getString(R.string.tour_location_search_fourth));
            urlBuilder.append(Integer.toString(NEAR_SEARCH_DISTANCE));
            urlBuilder.append(getString(R.string.tour_location_search_fifth));
            GetXMLTask task = new GetXMLTask();
            task.execute(urlBuilder.toString());
        } catch (Exception ex) {
            Log.d(LOGTAG, "refresh parsing failed");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(AppIndex.API).build();
        }
        createLocationRequest();


        appDatabase = FirebaseDatabase.getInstance().getReference();


        markerAchievementList = new ArrayList<>();
        markerBookmarkList = new ArrayList<>();
        trophyList = new ArrayList<>();
        glideRequestManager = Glide.with(this);

        achievementRecyclerView.setHasFixedSize(true);
        achievementLayoutManager = new LinearLayoutManager(this);
        achievementRecyclerView.setLayoutManager(achievementLayoutManager);
        achievementAdapter = new MapsLocationAdapter(this, markerAchievementList, glideRequestManager);
        achievementRecyclerView.setAdapter(achievementAdapter);
        achievementRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        bookmarkRecyclerView.setHasFixedSize(true);
        bookmarkLayoutManager = new LinearLayoutManager(this);
        bookmarkRecyclerView.setLayoutManager(bookmarkLayoutManager);
        bookmarkAdapter = new MapsLocationAdapter(this, markerBookmarkList, glideRequestManager);
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);
        bookmarkRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        trophyRecyclerView.setHasFixedSize(true);
        trophyLayoutManager = new LinearLayoutManager(this);
        trophyRecyclerView.setLayoutManager(trophyLayoutManager);
        trophyAdapter = new AchievementAdapter(this, trophyList, glideRequestManager);
        trophyRecyclerView.setAdapter(trophyAdapter);
        trophyRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        achievementTabLayout = (TabLayout) findViewById(R.id.tl_tablayout);
        achievementTabLayout.addOnTabSelectedListener(this);

        lastLocation = new Location(LocationManager.GPS_PROVIDER);
        lastLocation.setLongitude(121);
        lastLocation.setLatitude(37);

    }
    public void openPlacePicker(View view) {

        //___________create object of placepicker builder
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {

            //__________start placepicker activity for result
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //____________check for successfull result
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                //_________case for placepicker
                case PLACE_PICKER_REQUEST:

                    //______create place object from the received intent.
                    Place place = PlacePicker.getPlace(data, this);

                    //______get place name from place object
                    String toastMsg = String.format("Place: %s", place.getName());

                    //_________show toast message for selected place
                    Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                    break;
            }
        }
    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(googleApiClient, getIndexApiAction());
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(googleApiClient, getIndexApiAction());
    }

    @Override
    protected void onResume() {
        super.onResume();

        appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);
                        markerBookmarkList.clear();
                        trophyList.clear();
                        markerBookmarkList.addAll(user.getBookmarkList());
                        trophyList.addAll(user.getAchievementList());
                        addMarker();
                        bookmarkAdapter.notifyDataSetChanged();
                        trophyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error = " + databaseError);
                    }
                });
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
        achievementMap = googleMap;
        achievementMap.setMyLocationEnabled(true);
        achievementMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Add a marker in Sydney and move the camera
        achievementMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int markerIndex = markerHashMap.get(marker);
                Intent intent = new Intent(getApplicationContext(), AchievementDetailActivity.class);
                if(markerIndex < markerAchievementList.size()) {
                    intent.putExtra("contentid", markerAchievementList.get(markerIndex).getContentId());
                    intent.putExtra("contenttypeid", markerAchievementList.get(markerIndex).getContentTypeId());
                    intent.putExtra("distance", markerAchievementList.get(markerIndex).getDistance());
                    intent.putExtra("mapx", markerAchievementList.get(markerIndex).getLng());
                    intent.putExtra("mapy", markerAchievementList.get(markerIndex).getLat());
                } else {
                    markerIndex -= markerAchievementList.size();
                    intent.putExtra("contentid", markerBookmarkList.get(markerIndex).getContentId());
                    intent.putExtra("contenttypeid", markerBookmarkList.get(markerIndex).getContentTypeId());
                    intent.putExtra("distance", markerBookmarkList.get(markerIndex).getDistance());
                    intent.putExtra("mapx", markerBookmarkList.get(markerIndex).getLng());
                    intent.putExtra("mapy", markerBookmarkList.get(markerIndex).getLat());
                }
                startActivity(intent);
            }

        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (lastLocation != null) {
        }
        startLocationUpdates();
        trackerButtonClick();
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOGTAG, "connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOGTAG, "connection failed");
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        for (Achievement a : markerAchievementList) {

            Location achievementLocation = new Location(LocationManager.GPS_PROVIDER);
            achievementLocation.setLongitude(a.getLng());
            achievementLocation.setLatitude(a.getLat());
            double distance;
            distance = location.distanceTo(achievementLocation);
            a.setDistance(distance);
        }
        achievementAdapter.notifyDataSetChanged();
        addMarker();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition() == 0){
            achievementRecyclerView.setVisibility(View.GONE);
            bookmarkRecyclerView.setVisibility(View.GONE);
            trophyRecyclerView.setVisibility(View.GONE);
            trackerFloatingButton.setVisibility(View.VISIBLE);
            refreshFloatingButton.setVisibility(View.VISIBLE);
            searchFloatingButton.setVisibility(View.GONE);
            searchWindowEditText.setVisibility(View.GONE);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.show(mapFragment).commit();

        } else if(tab.getPosition() == 1){
            achievementRecyclerView.setVisibility(View.VISIBLE);
            bookmarkRecyclerView.setVisibility(View.GONE);
            trophyRecyclerView.setVisibility(View.GONE);
            trackerFloatingButton.setVisibility(View.GONE);
            refreshFloatingButton.setVisibility(View.GONE);
            searchFloatingButton.setVisibility(View.VISIBLE);
            searchWindowEditText.setVisibility(View.VISIBLE);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(mapFragment).commit();


        } else if(tab.getPosition() == 2) {
            achievementRecyclerView.setVisibility(View.GONE);
            bookmarkRecyclerView.setVisibility(View.VISIBLE);
            trophyRecyclerView.setVisibility(View.GONE);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(mapFragment).commit();
            trackerFloatingButton.setVisibility(View.GONE);
            refreshFloatingButton.setVisibility(View.GONE);
            searchFloatingButton.setVisibility(View.GONE);
            searchWindowEditText.setVisibility(View.GONE);


        } else if(tab.getPosition() == 3) {
            achievementRecyclerView.setVisibility(View.GONE);
            bookmarkRecyclerView.setVisibility(View.GONE);
            trophyRecyclerView.setVisibility(View.VISIBLE);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(mapFragment).commit();
            trackerFloatingButton.setVisibility(View.GONE);
            refreshFloatingButton.setVisibility(View.GONE);
            searchFloatingButton.setVisibility(View.GONE);
            searchWindowEditText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    private class GetXMLTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                parsingDocument = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
                parsingDocument.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return parsingDocument;
        }

        @Override
        protected void onPostExecute(Document doc) {

            String s = "";
            NodeList nodeList = doc.getElementsByTagName("item");
            markerAchievementList.clear();
            achievementMap.clear();
            for(int i = 0; i< nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;
                NodeList contentTypeId = fstElmnt.getElementsByTagName("contenttypeid");
                if(!(contentTypeId.item(0).getChildNodes().item(0).getNodeValue().equals("25"))) {
                    Log.d("whynot", contentTypeId.item(0).getChildNodes().item(0).getNodeValue());
                    NodeList sumnail = fstElmnt.getElementsByTagName("firstimage");
                    NodeList mapX = fstElmnt.getElementsByTagName("mapx");
                    NodeList mapY = fstElmnt.getElementsByTagName("mapy");
                    NodeList title = fstElmnt.getElementsByTagName("title");
                    NodeList address = fstElmnt.getElementsByTagName("addr1");
                    NodeList contentId = fstElmnt.getElementsByTagName("contentid");
                    String sumnailString;
                    String titleString;
                    String addressString;
                    String contentIdString;
                    String contentTypeIdString;

                    try {
                        sumnailString = sumnail.item(0).getChildNodes().item(0).getNodeValue();
                        Log.d("isok", sumnailString);
                    } catch (Exception ex) {
                        sumnailString = null;
                        Log.d("isok", "imagenull");
                    }

                    double x = Double.valueOf(mapX.item(0).getChildNodes().item(0).getNodeValue());
                    double y = Double.valueOf(mapY.item(0).getChildNodes().item(0).getNodeValue());
                    titleString = title.item(0).getChildNodes().item(0).getNodeValue();
                    addressString = address.item(0).getChildNodes().item(0).getNodeValue();
                    Location location = new Location(LocationManager.GPS_PROVIDER);
                    location.setLongitude(x);
                    location.setLatitude(y);
                    double distance;
                    try {
                        distance = currentLocation.distanceTo(location);
                    } catch (Exception ex){
                        distance = lastLocation.distanceTo(location);
                    }
                    contentIdString = contentId.item(0).getChildNodes().item(0).getNodeValue();
                    contentTypeIdString = contentTypeId.item(0).getChildNodes().item(0).getNodeValue();

                    LatLng pinPosition = new LatLng(y, x);
                    markerAchievementList.add(new Achievement(contentIdString, contentTypeIdString, null, titleString, addressString,
                            null, null, "", x, y, sumnailString, distance));
                }

            }
            addMarker();
            achievementAdapter.notifyDataSetChanged();

            super.onPostExecute(doc);
        }


    }//end inner class - GetXMLTask

    public void addMarker(){
        achievementMap.clear();
        markerHashMap = new HashMap<Marker, Integer>();
        int i = 0;
        double lat = 0, lng = 0;
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.placeholder);

        for(Achievement a : markerAchievementList){
            LatLng pinPosition = new LatLng(a.getLat(), a.getLng());
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLongitude(a.getLng());
            location.setLatitude(a.getLat());
            double distance;


            try {
                distance = currentLocation.distanceTo(location);
            } catch (Exception ex){
                distance = lastLocation.distanceTo(location);
            }
            String pinDistance;

            if( distance > 1000) {
                pinDistance = String.format("%.2f" , distance/1000) + "km";
            } else {
                pinDistance = (int)distance + "m";
            }

            Marker marker = achievementMap.addMarker(new MarkerOptions()
                    .title(a.getTitle())
                    .snippet(pinDistance)
                    .position(pinPosition));

            for(Achievement b : markerBookmarkList) {
                if(a.getLat() == b.getLat() && a.getLng() == b.getLng()) marker.setIcon(icon);
            }

            markerHashMap.put(marker, i++);
        }
    }


}

