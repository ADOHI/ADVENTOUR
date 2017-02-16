package com.example.adohi.adventour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.adohi.adventour.db.User;
import com.example.adohi.adventour.service.LocationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends NMapActivity implements TabLayout.OnTabSelectedListener{
//NMAP
    private static final String LOG_TAG = "NMapViewer";
    private static final boolean DEBUG = false;
    private NMapView mMapView;
    private NMapController mMapController;
    //private MapContainerView mMapContainerView;

    private static NGeoPoint NMAP_LOCATION_DEFAULT;
    private static final int NMAP_ZOOMLEVEL_DEFAULT = 11;
    private static final int NMAP_VIEW_MODE_DEFAULT = NMapView.VIEW_MODE_VECTOR;
    private static final boolean NMAP_TRAFFIC_MODE_DEFAULT = false;
    private static final boolean NMAP_BICYCLE_MODE_DEFAULT = false;

    private static final String KEY_ZOOM_LEVEL = "NMapViewer.zoomLevel";
    private static final String KEY_CENTER_LONGITUDE = "NMapViewer.centerLongitudeE6";
    private static final String KEY_CENTER_LATITUDE = "NMapViewer.centerLatitudeE6";
    private static final String KEY_VIEW_MODE = "NMapViewer.viewMode";
    private static final String KEY_TRAFFIC_MODE = "NMapViewer.trafficMode";
    private static final String KEY_BICYCLE_MODE = "NMapViewer.bicycleMode";

    private SharedPreferences mPreferences;

    private NMapOverlayManager mOverlayManager;

    private NMapMyLocationOverlay mMyLocationOverlay;
    private static NMapLocationManager nMapLocationManager;
    private NMapCompassManager mMapCompassManager;

    private NMapViewerResourceProvider mMapViewerResourceProvider;

    private NMapPOIdataOverlay mFloatingPOIdataOverlay;
    private NMapPOIitem mFloatingPOIitem;

    private NMapPOIdata nearPoiData;
    private NMapPOIdata bookmarkPoiData;
//NMAP


    private RequestManager mGlideRequestManager;
    @BindView(R.id.bt_refresh)FloatingActionButton refreshFloatingButton;
    @OnClick(R.id.bt_refresh)void refreshButtonClick(){
        StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList");
        urlBuilder.append("?" + "ServiceKey" + "=JNbqf4NQaSTqFcIueZ7tna%2B1OvOKTiRGmCMpdoL%2FxlE4YUkZPfVhxk9rwarKYNACs1UfYGj49jO%2BKFYKSqsFhQ%3D%3D");
        urlBuilder.append("&contentTypeId=" + "&mapX" + "=" + String.format("%.6f", mMapController.getMapCenter().getLongitude()));
        urlBuilder.append("&mapY" + "=" + String.format("%.6f", mMapController.getMapCenter().getLatitude()));
        urlBuilder.append("&radius" + "=" + Integer.toString(1000));
        urlBuilder.append("&listYN=Y&MobileOS=AND&MobileApp=TourAPI3.0_Guide&arrange=B&numOfRows=12&pageNo=1");
        GetXMLTask task = new GetXMLTask();
        task.execute(urlBuilder.toString());
    }

    @BindView(R.id.bt_tracker)FloatingActionButton trackerFloatingButton;
    @OnClick(R.id.bt_tracker)void trackerButtonClick(){
        if (mMapController != null) {
            try{
                mMapController.animateTo(nMapLocationManager.getMyLocation());
            }catch (Exception ex){
                mMapController.animateTo(NMAP_LOCATION_DEFAULT);
            }
        }
        restoreInstanceState();
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
   // @BindView(R.id.ti_bookmark)TabItem bookmarkTabItem;
    //@BindView(R.id.ti_map)TabItem mapTabItem;
    @BindView(R.id.rv_achievement)RecyclerView mRecyclerView;
    @BindView(R.id.rv_bookmark_achievement)RecyclerView mBookmarkRecyclerView;
    /*@OnClick(R.id.ti_bookmark)void bookmarkTabItemClick(){
        mBookmarkRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }
    @OnClick(R.id.ti_bookmark)void mapTabItemClick(){
        mBookmarkRecyclerView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }*/
    //private TabItem bookmarkTabItem;
    private TabLayout tabLayout;
    Document doc = null;


    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mBookmarkAdapter;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mBookmarkLayoutManager;


    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> sumnailList = new ArrayList<>();
    private ArrayList<String> addressList = new ArrayList<>();
    private ArrayList<String> distanceList = new ArrayList<>();
    private ArrayList<String> contentIdList = new ArrayList<>();
    private ArrayList<String> contentTypeIdList = new ArrayList<>();
    private ArrayList<NGeoPoint> nGeoPointList = new ArrayList<>();

    private ArrayList<String> bookmarkTitleList = new ArrayList<>();
    private ArrayList<String> bookmarkSumnailList = new ArrayList<>();
    private ArrayList<String> bookmarkAddressList = new ArrayList<>();
    private ArrayList<String> bookmarkDistanceList = new ArrayList<>();
    private ArrayList<String> bookmarkContentIdList = new ArrayList<>();
    private ArrayList<String> bookmarkContentTypeIdList = new ArrayList<>();
    private ArrayList<NGeoPoint> bookmarkNGeoPointList = new ArrayList<>();

    private DatabaseReference mDatabase;
    private String uid;

    private ViewPager viewPager = null;

    private TabLayout mTabLayout;
    private TabItem mapTabitem;
    private TabItem bookmarkTabitem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Log.d("abcdefg", Double.toString(intent.getExtras().getDouble("y")));

        NMAP_LOCATION_DEFAULT = new NGeoPoint(intent.getExtras().getDouble("x"), intent.getExtras().getDouble("y"));

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
                        Log.d("testkkk", "" + user.bookmarkIdList.size());

                        bookmarkPoiData.removeAllPOIdata();
                        bookmarkTitleList.clear();
                        bookmarkSumnailList.clear();
                        bookmarkAddressList.clear();
                        bookmarkDistanceList.clear();
                        bookmarkContentIdList.clear();
                        bookmarkContentTypeIdList.clear();
                        bookmarkNGeoPointList.clear();
                        if(user.bookmarkIdList.size() == 0){
                            mBookmarkAdapter.notifyDataSetChanged();
                        }
                        for(String n : user.bookmarkIdList){
                            StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon");
                            urlBuilder.append("?" + "ServiceKey" + "=JNbqf4NQaSTqFcIueZ7tna%2B1OvOKTiRGmCMpdoL%2FxlE4YUkZPfVhxk9rwarKYNACs1UfYGj49jO%2BKFYKSqsFhQ%3D%3D");
                            urlBuilder.append("&contentTypeId=");
                            urlBuilder.append("&contentId" + "=" + n);
                            urlBuilder.append("&MobileOS=AND&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y");
                            GetBookmarkXMLTask getBookmarkXMLTask= new GetBookmarkXMLTask();
                            getBookmarkXMLTask.execute(urlBuilder.toString());
                        }
                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        String clientId = "hVjlD1e4VUCjEqzFBtpC";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "B5_3wPIhlm";//애플리케이션 클라이언트 시크릿값";

        mMapView = (NMapView)findViewById(R.id.mapView);
        mMapView.setClientId(clientId);

        // initialize map view
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
        mMapView.setOnMapViewDelegate(onMapViewTouchDelegate);

        // use map controller to zoom in/out, pan and set map center, zoom level etc.
        mMapController = mMapView.getMapController();

        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        // set data provider listener
       // super.setMapDataProviderListener(onDataProviderListener);

        // create overlay manager
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
        // register callout overlay listener to customize it.
        mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);
        // register callout overlay view listener to customize it.
        mOverlayManager.setOnCalloutOverlayViewListener(onCalloutOverlayViewListener);

        // location manager
        nMapLocationManager = new NMapLocationManager(this);
        nMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
        
        
        // compass manager
        mMapCompassManager = new NMapCompassManager(this);
        // create my location overlay
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(LocationService.mapLocationManager, mMapCompassManager);
        mMyLocationOverlay.refresh();
        if(mMyLocationOverlay.hasPathData()){
            Log.d("aaaaa", "bbbbb");
        }
        if(mMyLocationOverlay == null){
            Log.d("aaaaa", "bbbbb");
        } else

        startMyLocation();

        nearPoiData = new NMapPOIdata(30, mMapViewerResourceProvider);
        bookmarkPoiData = new NMapPOIdata(3, mMapViewerResourceProvider);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mGlideRequestManager = Glide.with(this);
        mAdapter = new MyAdapter(this, titleList, sumnailList, addressList, distanceList,
                 contentIdList, contentTypeIdList, nGeoPointList, mGlideRequestManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mBookmarkLayoutManager = new LinearLayoutManager(this);
        mBookmarkRecyclerView.setLayoutManager(mBookmarkLayoutManager);

        mBookmarkAdapter = new MyAdapter(this, bookmarkTitleList, bookmarkSumnailList, bookmarkAddressList,
                bookmarkDistanceList, bookmarkContentIdList, bookmarkContentTypeIdList,
                bookmarkNGeoPointList, mGlideRequestManager);
        mBookmarkRecyclerView.setAdapter(mBookmarkAdapter);
        mBookmarkRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mTabLayout = (TabLayout)findViewById(R.id.tl_tablayout);
        mapTabitem = (TabItem) findViewById(R.id.ti_map);
        bookmarkTabitem = (TabItem) findViewById(R.id.ti_bookmark);
        mTabLayout.addOnTabSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {

        stopMyLocation();

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        // save map view state such as map center position and zoom level.
        //saveInstanceState();

        super.onDestroy();
    }

	/* Test Functions */

    private void startMyLocation() {

        if (mMyLocationOverlay != null) {
            if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
                mOverlayManager.addOverlay(mMyLocationOverlay);
            }

            if (nMapLocationManager.isMyLocationEnabled()) {
                stopMyLocation();
                mMapView.postInvalidate();
            } else {
                boolean isMyLocationEnabled = nMapLocationManager.enableMyLocation(true);
                if (!isMyLocationEnabled) {
                    Toast.makeText(MainActivity.this, "Please enable a My Location source in system settings",
                            Toast.LENGTH_LONG).show();

                    Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(goToSettings);

                    return;
                }
            }
        }
    }

    private void stopMyLocation() {
        if (mMyLocationOverlay != null) {
            nMapLocationManager.disableMyLocation();

        }
    }


    private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

        @Override
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onCalloutClick: title=" + item.getTitle());
            }

            // [[TEMP]] handle a click event of the callout
            Toast.makeText(MainActivity.this, "onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            if (DEBUG) {
                if (item != null) {
                    Log.i(LOG_TAG, "onFocusChanged: " + item.toString());
                } else {
                    Log.i(LOG_TAG, "onFocusChanged: ");
                }
            }
        }
    };

    private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {

        @Override
        public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem,
                                                         Rect itemBounds) {

            // handle overlapped items
            if (itemOverlay instanceof NMapPOIdataOverlay) {
                NMapPOIdataOverlay poiDataOverlay = (NMapPOIdataOverlay)itemOverlay;

                // check if it is selected by touch event
                if (!poiDataOverlay.isFocusedBySelectItem()) {
                    int countOfOverlappedItems = 1;

                    NMapPOIdata poiData = poiDataOverlay.getPOIdata();
                    for (int i = 0; i < poiData.count(); i++) {
                        NMapPOIitem poiItem = poiData.getPOIitem(i);

                        // skip selected item
                        if (poiItem == overlayItem) {
                            continue;
                        }

                        // check if overlapped or not
                        if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
                            countOfOverlappedItems++;
                        }
                    }

                    if (countOfOverlappedItems > 1) {
                        String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
                        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
            }

            // use custom old callout overlay
            if (overlayItem instanceof NMapPOIitem) {
                NMapPOIitem poiItem = (NMapPOIitem)overlayItem;

                if (poiItem.showRightButton()) {
                    return new NMapCalloutCustomOldOverlay(itemOverlay, overlayItem, itemBounds,
                            mMapViewerResourceProvider);
                }
            }

            // use custom callout overlay
            return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

            // set basic callout overlay
            //return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
        }

    };

    private final NMapOverlayManager.OnCalloutOverlayViewListener onCalloutOverlayViewListener = new NMapOverlayManager.OnCalloutOverlayViewListener() {

        @Override
        public View onCreateCalloutOverlayView(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

            if (overlayItem != null) {
                // [TEST] 말풍선 오버레이를 뷰로 설정함
                String title = overlayItem.getTitle();
                if (title != null && title.length() > 5) {
                    return new NMapCalloutCustomOverlayView(MainActivity.this, itemOverlay, overlayItem, itemBounds);
                }
            }

            // null을 반환하면 말풍선 오버레이를 표시하지 않음
            return null;
        }

    };


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d("tablisten", ""+tab.getPosition());
        if(tab.getPosition() == 0){
            mRecyclerView.setVisibility(View.VISIBLE);
            mBookmarkRecyclerView.setVisibility(View.GONE);
            mOverlayManager.clearOverlays();
            // create POI data overlay
            NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(nearPoiData, null);
            poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
            poiDataOverlay.showAllPOIdata(0);
        } else if(tab.getPosition() == 1) {
            mRecyclerView.setVisibility(View.GONE);
            mBookmarkRecyclerView.setVisibility(View.VISIBLE);
            mOverlayManager.clearOverlays();
            if(bookmarkPoiData.count() != 0) {
                NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(bookmarkPoiData, null);
                poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
                poiDataOverlay.showAllPOIdata(0);
            }

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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
                    nearPoiData.removeAllPOIdata();
                    nearPoiData.beginPOIdata(30);
                    titleList.clear();
                    sumnailList.clear();
                    addressList.clear();
                    distanceList.clear();
                    contentIdList.clear();
                    contentTypeIdList.clear();
                    nGeoPointList.clear();

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

                            //<wfKor>맑음</wfKor> =====> <wfKor> 태그의 첫번째 자식노드는 TextNode 이고 TextNode의 값은 맑음
                            int markerId = NMapPOIflagType.PIN;
                            try {
                                Log.d("isok", sumnail.item(0).getChildNodes().item(0).getNodeValue());
                                sumnailList.add(sumnail.item(0).getChildNodes().item(0).getNodeValue());
                            } catch (Exception ex) {
                                Log.d("isok", "imagenull");
                                sumnailList.add(null);
                            }
                            // set POI data
                            double x = Double.valueOf(mapX.item(0).getChildNodes().item(0).getNodeValue());
                            double y = Double.valueOf(mapY.item(0).getChildNodes().item(0).getNodeValue());
                            NMapPOIitem item = nearPoiData.addPOIitem(x, y, title.item(0).getChildNodes().item(0).getNodeValue(), markerId, 0);
                            item.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);
                            titleList.add(title.item(0).getChildNodes().item(0).getNodeValue());
                            addressList.add(address.item(0).getChildNodes().item(0).getNodeValue());
                            NGeoPoint nGeoPoint = new NGeoPoint(x, y);
                            nGeoPointList.add(nGeoPoint);
                            try {
                                distanceList.add(Integer.toString((int) NGeoPoint.getDistance(nMapLocationManager.getMyLocation(), nGeoPoint)) + "m");
                            } catch (Exception ex){
                                distanceList.add(Integer.toString((int) NGeoPoint.getDistance(NMAP_LOCATION_DEFAULT, nGeoPoint)) + "m");
                            }
                            contentIdList.add(contentId.item(0).getChildNodes().item(0).getNodeValue());
                            contentTypeIdList.add(contentTypeId.item(0).getChildNodes().item(0).getNodeValue());
                        }
                    }

                    mAdapter.notifyDataSetChanged();

                    nearPoiData.endPOIdata();

                    mOverlayManager.clearOverlays();
                    // create POI data overlay
                    NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(nearPoiData, null);

                    // set event listener to the overlay
                    poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

                    // select an item
                    //poiDataOverlay.selectPOIitem(0, false);

                    poiDataOverlay.showAllPOIdata(0);
                    Log.d("overlay", ""+nearPoiData.count());
                    super.onPostExecute(doc);
                }
                
                
            }//end inner class - GetXMLTask

    private class GetBookmarkXMLTask extends AsyncTask<String, Void, Document> {

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
            bookmarkPoiData.beginPOIdata(3);

            for(int i = 0; i< nodeList.getLength(); i++) {
                Node node = nodeList.item(i); //data엘리먼트 노드
                Element fstElmnt = (Element) node;
                NodeList contentTypeId = fstElmnt.getElementsByTagName("contenttypeid");
                if (!(contentTypeId.item(0).getChildNodes().item(0).getNodeValue().equals("25"))) {
                    Log.d("whynot", contentTypeId.item(0).getChildNodes().item(0).getNodeValue());
                    NodeList sumnail = fstElmnt.getElementsByTagName("firstimage");
                    NodeList mapX = fstElmnt.getElementsByTagName("mapx");
                    NodeList mapY = fstElmnt.getElementsByTagName("mapy");
                    NodeList title = fstElmnt.getElementsByTagName("title");
                    NodeList address = fstElmnt.getElementsByTagName("addr1");
                    NodeList contentId = fstElmnt.getElementsByTagName("contentid");

                    int markerId = NMapPOIflagType.PIN;
                    try {
                        Log.d("isok", sumnail.item(0).getChildNodes().item(0).getNodeValue());
                        bookmarkSumnailList.add(sumnail.item(0).getChildNodes().item(0).getNodeValue());
                    } catch (Exception ex) {
                        Log.d("isok", "imagenull");
                        bookmarkSumnailList.add(null);
                    }
                    // set POI data
                    double x = Double.valueOf(mapX.item(0).getChildNodes().item(0).getNodeValue());
                    double y = Double.valueOf(mapY.item(0).getChildNodes().item(0).getNodeValue());
                    NMapPOIitem item = bookmarkPoiData.addPOIitem(x, y, title.item(0).getChildNodes().item(0).getNodeValue(), markerId, 0);
                    item.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);
                    bookmarkTitleList.add(title.item(0).getChildNodes().item(0).getNodeValue());
                    bookmarkAddressList.add(address.item(0).getChildNodes().item(0).getNodeValue());
                    NGeoPoint nGeoPoint = new NGeoPoint(x, y);
                    bookmarkNGeoPointList.add(nGeoPoint);
                    try {
                        bookmarkDistanceList.add(Integer.toString((int) NGeoPoint.getDistance(nMapLocationManager.getMyLocation(), nGeoPoint)) + "m");
                    } catch (Exception ex) {
                        bookmarkDistanceList.add(Integer.toString((int) NGeoPoint.getDistance(NMAP_LOCATION_DEFAULT, nGeoPoint)) + "m");
                    }
                    bookmarkContentIdList.add(contentId.item(0).getChildNodes().item(0).getNodeValue());
                    bookmarkContentTypeIdList.add(contentTypeId.item(0).getChildNodes().item(0).getNodeValue());
                }
            }

            mBookmarkAdapter.notifyDataSetChanged();

            bookmarkPoiData.endPOIdata();

            if(mTabLayout.getSelectedTabPosition() == 1){
                mOverlayManager.clearOverlays();
                NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(bookmarkPoiData, null);
                poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
                poiDataOverlay.showAllPOIdata(0);
            }
            Log.d("overlay", ""+bookmarkPoiData.count());
            super.onPostExecute(doc);
    }


    }//end inner class - GetXMLTask

    private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {

        @Override
        public boolean isLocationTracking() {
            if (nMapLocationManager != null) {
                if (nMapLocationManager.isMyLocationEnabled()) {
                    return nMapLocationManager.isMyLocationFixed();
                }
            }
            return false;
        }

    };


    /* MapView State Change Listener*/
    private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

        @Override
        public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

            if (errorInfo == null) { // success
                // restore map view state such as map center position and zoom level.
                restoreInstanceState();

            } else { // fail
                Log.e(LOG_TAG, "onFailedToInitializeWithError: " + errorInfo.toString());

            }
        }
        @Override
        public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
            }
        }

        @Override
        public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onMapCenterChange: center=" + center.toString());
            }
        }

        @Override
        public void onZoomLevelChange(NMapView mapView, int level) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onZoomLevelChange: level=" + level);
            }
        }

        @Override
        public void onMapCenterChangeFine(NMapView mapView) {

        }
    };

    private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {

        @Override
        public void onLongPress(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLongPressCanceled(NMapView mapView) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSingleTapUp(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTouchDown(NMapView mapView, MotionEvent ev) {

        }

        @Override
        public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {
        }

        @Override
        public void onTouchUp(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

    };

    private void restoreInstanceState() {
        mPreferences = getPreferences(MODE_PRIVATE);

        int longitudeE6 = mPreferences.getInt(KEY_CENTER_LONGITUDE, NMAP_LOCATION_DEFAULT.getLongitudeE6());
        int latitudeE6 = mPreferences.getInt(KEY_CENTER_LATITUDE, NMAP_LOCATION_DEFAULT.getLatitudeE6());
        int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
        int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
        boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
        boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);
        Log.d("nmap", "" + longitudeE6 + "   " + latitudeE6);
        mMapController.setMapViewMode(viewMode);
        mMapController.setMapViewTrafficMode(trafficMode);
        mMapController.setMapViewBicycleMode(bicycleMode);
        mMapController.setMapCenter(new NGeoPoint(longitudeE6, latitudeE6), level);


    }

   private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

            Log.d("aaasss", "aaasssdd");
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

            Toast.makeText(MainActivity.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

            Toast.makeText(MainActivity.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();

            stopMyLocation();
        }

    };
}
