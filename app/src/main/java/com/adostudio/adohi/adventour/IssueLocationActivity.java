package com.adostudio.adohi.adventour;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Achievement;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IssueLocationActivity extends AppCompatActivity {

    private ArrayList<Achievement> nearAchievementArrayList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RequestManager mGlideRequestManager;
    private Document doc = null;
    private MyApplication myApplication;
    @BindView(R.id.rv_issue_location)RecyclerView issueLocationRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_location);
        ButterKnife.bind(this);
        myApplication = (MyApplication)getApplication();
        mGlideRequestManager = Glide.with(this);
        nearAchievementArrayList = new ArrayList<>();
        issueLocationRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        issueLocationRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(this, nearAchievementArrayList, mGlideRequestManager);
        issueLocationRecyclerView.setAdapter(mAdapter);
        issueLocationRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList");
        urlBuilder.append("?" + "ServiceKey" + "=JNbqf4NQaSTqFcIueZ7tna%2B1OvOKTiRGmCMpdoL%2FxlE4YUkZPfVhxk9rwarKYNACs1UfYGj49jO%2BKFYKSqsFhQ%3D%3D");
        urlBuilder.append("&contentTypeId=" + "&mapX" + "=" + String.format("%.6f",  myApplication.getCurrentLng()));
        urlBuilder.append("&mapY" + "=" + String.format("%.6f", myApplication.getCurrentLat()));
        urlBuilder.append("&radius" + "=" + Integer.toString(100));
        urlBuilder.append("&listYN=Y&MobileOS=AND&MobileApp=TourAPI3.0_Guide&arrange=E&numOfRows=999&pageNo=1");
        GetXMLTask task = new GetXMLTask();
        task.execute(urlBuilder.toString());
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
            NodeList nodeList = doc.getElementsByTagName("item");
            nearAchievementArrayList.clear();

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
                    } catch (Exception ex) {
                        sumnailString = null;
                    }

                    double x = Double.valueOf(mapX.item(0).getChildNodes().item(0).getNodeValue());
                    double y = Double.valueOf(mapY.item(0).getChildNodes().item(0).getNodeValue());
                    titleString = title.item(0).getChildNodes().item(0).getNodeValue();
                    addressString = address.item(0).getChildNodes().item(0).getNodeValue();
                    Location location = new Location(LocationManager.GPS_PROVIDER);
                    location.setLongitude(x);
                    location.setLatitude(y);
                    double distance = 0;
                    Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
                    currentLocation.setLongitude(myApplication.getCurrentLng());
                    currentLocation.setLatitude(myApplication.getCurrentLat());
                    try {
                        distance = currentLocation.distanceTo(location);
                    } catch (Exception ex){
                        Log.d("location", "not detected");
                    }
                    contentIdString = contentId.item(0).getChildNodes().item(0).getNodeValue();
                    contentTypeIdString = contentTypeId.item(0).getChildNodes().item(0).getNodeValue();
                    nearAchievementArrayList.add(new Achievement(contentIdString, contentTypeIdString, null, titleString, addressString,
                            null, null, "", x, y, sumnailString, distance));
                }
            }
            mAdapter.notifyDataSetChanged();
            super.onPostExecute(doc);
        }


    }
}
