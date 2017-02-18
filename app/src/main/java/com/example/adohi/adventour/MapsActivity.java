package com.example.adohi.adventour;


import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.adohi.adventour.db.User;
import com.example.adohi.adventour.db.achieve;
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
import com.google.android.gms.maps.model.CameraPosition;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
        ,TabLayout.OnTabSelectedListener, GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private PendingResult<LocationSettingsResult> result;
    private Fragment mapview;
    final int PLACE_PICKER_REQUEST = -1;
    Marker mPositionMarker;
    private ArrayList<achieve> markerAchieveList;
    private ArrayList<achieve> markerBookmarkList;
    private ArrayList<achieve> trophyList;
    @BindView(R.id.bt_tracker)FloatingActionButton trackerFloatingButton;
    @OnClick(R.id.bt_tracker)void trackerButtonClick(){
        LatLng target;
        try {
            target = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        } catch (Exception ex){
            target = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        CameraPosition position = this.mMap.getCameraPosition();

        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.zoom(15);
        builder.target(target);

        this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
    }
    @BindView(R.id.et_searchwindow)EditText searchWindowEditText;
    @BindView(R.id.bt_search)FloatingActionButton searchFloatingButton;
    @OnClick(R.id.bt_search)void searchButtonClick()  {
        try {
            StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchKeyword");
            urlBuilder.append("?" + "ServiceKey" + "=JNbqf4NQaSTqFcIueZ7tna%2B1OvOKTiRGmCMpdoL%2FxlE4YUkZPfVhxk9rwarKYNACs1UfYGj49jO%2BKFYKSqsFhQ%3D%3D");
            urlBuilder.append("&keyword=" + URLEncoder.encode(searchWindowEditText.getText().toString(), "utf-8"));
            urlBuilder.append("&areaCode=&sigunguCode=&cat1=&cat2=&cat3=&listYN=Y&MobileOS=AND&MobileApp=TourAPI3.0_Guide&arrange=B&numOfRows=12&pageNo=1");
            GetXMLTask task = new GetXMLTask();
            task.execute(urlBuilder.toString());
        }catch (Exception ex){}

    }

    @BindView(R.id.rv_achievement)RecyclerView mRecyclerView;
    @BindView(R.id.rv_bookmark_achievement)RecyclerView mBookmarkRecyclerView;
    @BindView(R.id.rv_trophy_achievement)RecyclerView mTrophyRecyclerView;

    private TabLayout tabLayout;
    Document doc = null;


    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mBookmarkAdapter;
    private RecyclerView.Adapter mTrophyAdapter;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mBookmarkLayoutManager;
    private RecyclerView.LayoutManager mTrophyLayoutManager;

    private ArrayList<String> trophyTitleList = new ArrayList<>();
    private ArrayList<String> trophySumnailList = new ArrayList<>();
    private ArrayList<String> trophyAddressList = new ArrayList<>();
    private ArrayList<String> trophyDistanceList = new ArrayList<>();
    private ArrayList<String> trophyContentIdList = new ArrayList<>();
    private ArrayList<String> trophyContentTypeIdList = new ArrayList<>();
    private ArrayList<Location> trophyLocationList = new ArrayList<>();

    private DatabaseReference mDatabase;
    private String uid;

    private ViewPager viewPager = null;

    private TabLayout mTabLayout;

    private SupportMapFragment mapFragment;

    private RequestManager mGlideRequestManager;
    @BindView(R.id.bt_refresh)FloatingActionButton refreshFloatingButton;
    @OnClick(R.id.bt_refresh)void refreshButtonClick(){
        StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList");
        urlBuilder.append("?" + "ServiceKey" + "=JNbqf4NQaSTqFcIueZ7tna%2B1OvOKTiRGmCMpdoL%2FxlE4YUkZPfVhxk9rwarKYNACs1UfYGj49jO%2BKFYKSqsFhQ%3D%3D");
        urlBuilder.append("&contentTypeId=" + "&mapX" + "=" + String.format("%.6f",  mMap.getCameraPosition().target.longitude));
        urlBuilder.append("&mapY" + "=" + String.format("%.6f", mMap.getCameraPosition().target.latitude));
        urlBuilder.append("&radius" + "=" + Integer.toString(1000));
        urlBuilder.append("&listYN=Y&MobileOS=AND&MobileApp=TourAPI3.0_Guide&arrange=B&numOfRows=12&pageNo=1");
        GetXMLTask task = new GetXMLTask();
        task.execute(urlBuilder.toString());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(AppIndex.API).build();
        }
        createLocationRequest();

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
        mDatabase.child("users").child(uid).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        markerBookmarkList.clear();
                        trophyList.clear();
                        for(achieve a : user.bookmarkList){
                            markerBookmarkList.add(a);
                        }
                        for(achieve b : user.achievementList){
                            trophyList.add(b);
                        }
                        mBookmarkAdapter.notifyDataSetChanged();
                        mTrophyAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        markerAchieveList = new ArrayList<>();
        markerBookmarkList = new ArrayList<>();
        trophyList = new ArrayList<>();
        mGlideRequestManager = Glide.with(this);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(this, markerAchieveList, mGlideRequestManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mBookmarkRecyclerView.setHasFixedSize(true);
        mBookmarkLayoutManager = new LinearLayoutManager(this);
        mBookmarkRecyclerView.setLayoutManager(mBookmarkLayoutManager);
        mBookmarkAdapter = new MyAdapter(this, markerBookmarkList, mGlideRequestManager);
        mBookmarkRecyclerView.setAdapter(mBookmarkAdapter);
        mBookmarkRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mTrophyRecyclerView.setHasFixedSize(true);
        mTrophyLayoutManager = new LinearLayoutManager(this);
        mTrophyRecyclerView.setLayoutManager(mTrophyLayoutManager);
        mTrophyAdapter = new MyAdapter(this, trophyList, mGlideRequestManager);
        mTrophyRecyclerView.setAdapter(mTrophyAdapter);
        mTrophyRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mTabLayout = (TabLayout) findViewById(R.id.tl_tablayout);
        mTabLayout.addOnTabSelectedListener(this);

        Intent getIntent = getIntent();
        mLastLocation = new Location(LocationManager.GPS_PROVIDER);
        mLastLocation.setLongitude(121);
        mLastLocation.setLatitude(37);






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
        mGoogleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
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

        // Add a marker in Sydney and move the camera
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("holy funcking", ""+mLastLocation.getLatitude());
        }
        startLocationUpdates();
        trackerButtonClick();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    private void updateUI() {

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d("tablisten", ""+tab.getPosition());
        if(tab.getPosition() == 0){
            mRecyclerView.setVisibility(View.GONE);
            mBookmarkRecyclerView.setVisibility(View.GONE);
            mTrophyRecyclerView.setVisibility(View.GONE);
            trackerFloatingButton.setVisibility(View.VISIBLE);
            refreshFloatingButton.setVisibility(View.VISIBLE);
            searchFloatingButton.setVisibility(View.GONE);
            searchWindowEditText.setVisibility(View.GONE);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.show(mapFragment).commit();

        } else if(tab.getPosition() == 1){
            mRecyclerView.setVisibility(View.VISIBLE);
            mBookmarkRecyclerView.setVisibility(View.GONE);
            mTrophyRecyclerView.setVisibility(View.GONE);
            trackerFloatingButton.setVisibility(View.GONE);
            refreshFloatingButton.setVisibility(View.GONE);
            searchFloatingButton.setVisibility(View.VISIBLE);
            searchWindowEditText.setVisibility(View.VISIBLE);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(mapFragment).commit();


        } else if(tab.getPosition() == 2) {
            mRecyclerView.setVisibility(View.GONE);
            mBookmarkRecyclerView.setVisibility(View.VISIBLE);
            mTrophyRecyclerView.setVisibility(View.GONE);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(mapFragment).commit();
            trackerFloatingButton.setVisibility(View.GONE);
            refreshFloatingButton.setVisibility(View.GONE);
            searchFloatingButton.setVisibility(View.GONE);
            searchWindowEditText.setVisibility(View.GONE);


        } else if(tab.getPosition() == 3) {
            mRecyclerView.setVisibility(View.GONE);
            mBookmarkRecyclerView.setVisibility(View.GONE);
            mTrophyRecyclerView.setVisibility(View.VISIBLE);
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

    @Override
    public void onInfoWindowClick(Marker marker) {
        try {
            StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchKeyword");
            urlBuilder.append("?" + "ServiceKey" + "=JNbqf4NQaSTqFcIueZ7tna%2B1OvOKTiRGmCMpdoL%2FxlE4YUkZPfVhxk9rwarKYNACs1UfYGj49jO%2BKFYKSqsFhQ%3D%3D");
            urlBuilder.append("&keyword=" + URLEncoder.encode(marker.getTitle(), "utf-8"));
            urlBuilder.append("&areaCode=&sigunguCode=&cat1=&cat2=&cat3=&listYN=Y&MobileOS=AND&MobileApp=TourAPI3.0_Guide&arrange=B&numOfRows=12&pageNo=1");
            GetXMLTask task = new GetXMLTask();
            task.execute(urlBuilder.toString());
        }catch (Exception ex){}

    }

    private class GetXMLTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {

            String s = "";
            //data태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
            NodeList nodeList = doc.getElementsByTagName("item");
            //data 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환
            //NMapPOIdata poiData = new NMapPOIdata(12, mMapViewerResourceProvider);
            markerAchieveList.clear();
            mMap.clear();
            for(int i = 0; i< nodeList.getLength(); i++){
                Node node = nodeList.item(i); //data엘리먼트 노드
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
                    //<wfKor>맑음</wfKor> =====> <wfKor> 태그의 첫번째 자식노드는 TextNode 이고 TextNode의 값은 맑음
                    try {
                        sumnailString = sumnail.item(0).getChildNodes().item(0).getNodeValue();
                        Log.d("isok", sumnailString);
                    } catch (Exception ex) {
                        sumnailString = null;
                        Log.d("isok", "imagenull");
                    }
                    // set POI data
                    double x = Double.valueOf(mapX.item(0).getChildNodes().item(0).getNodeValue());
                    double y = Double.valueOf(mapY.item(0).getChildNodes().item(0).getNodeValue());
                    titleString = title.item(0).getChildNodes().item(0).getNodeValue();
                    addressString = address.item(0).getChildNodes().item(0).getNodeValue();
                    Location location = new Location(LocationManager.GPS_PROVIDER);
                    location.setLongitude(x);
                    location.setLatitude(y);
                    double distance;
                    try {
                        distance = mCurrentLocation.distanceTo(location);
                    } catch (Exception ex){
                        distance = mLastLocation.distanceTo(location);
                    }
                    contentIdString = contentId.item(0).getChildNodes().item(0).getNodeValue();
                    contentTypeIdString = contentTypeId.item(0).getChildNodes().item(0).getNodeValue();

                    LatLng pinPosition = new LatLng(y, x);

                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

                    mMap.addMarker(new MarkerOptions()
                            .title(title.item(0).getChildNodes().item(0).getNodeValue())
                            .snippet(Integer.toString((int)distance)+"m")
                            .position(pinPosition));
                    markerAchieveList.add(new achieve(contentIdString, contentTypeIdString, null, titleString, addressString,
                            null, null, -1, x, y, sumnailString, distance));
                }
            }

            mAdapter.notifyDataSetChanged();

            super.onPostExecute(doc);
        }


    }//end inner class - GetXMLTask

}

