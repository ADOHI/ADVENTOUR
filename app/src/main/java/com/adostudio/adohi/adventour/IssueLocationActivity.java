package com.adostudio.adohi.adventour;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Achievement;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

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

public class
IssueLocationActivity extends AppCompatActivity {

    private static final String LOGTAG = "IssueLocationActivity";
    private static final int NEAR_LOCATION_DISTANCE = 100;
    private ArrayList<Achievement> nearAchievementArrayList;
    private RecyclerView.Adapter nearAchievementAdapter;
    private RecyclerView.LayoutManager nearAchievementLayoutManager;
    private RequestManager glideRequestManager;
    private Document parsingDocument = null;
    @BindView(R.id.rv_issue_location)RecyclerView issueLocationRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_location);
        ButterKnife.bind(this);
        glideRequestManager = Glide.with(this);
        nearAchievementArrayList = new ArrayList<>();
        issueLocationRecyclerView.setHasFixedSize(true);
        nearAchievementLayoutManager = new LinearLayoutManager(this);
        issueLocationRecyclerView.setLayoutManager(nearAchievementLayoutManager);
        nearAchievementAdapter = new MapsLocationAdapter(this, nearAchievementArrayList, glideRequestManager);
        issueLocationRecyclerView.setAdapter(nearAchievementAdapter);
        issueLocationRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        StringBuilder urlBuilder = new StringBuilder(getString(R.string.tour_location_search_first));
        urlBuilder.append(getString(R.string.tour_api_key));
        urlBuilder.append(getString(R.string.tour_location_search_second));
        urlBuilder.append(String.format("%.6f",  MyApplication.getCurrentLng()));
        urlBuilder.append(getString(R.string.tour_location_search_third));
        urlBuilder.append(String.format("%.6f", MyApplication.getCurrentLat()));
        urlBuilder.append(getString(R.string.tour_location_search_fourth));
        urlBuilder.append(Integer.toString(NEAR_LOCATION_DISTANCE));
        urlBuilder.append(getString(R.string.tour_location_search_fifth));
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
                DocumentBuilder db = dbf.newDocumentBuilder();
                parsingDocument = db.parse(new InputSource(url.openStream()));
                parsingDocument.getDocumentElement().normalize();
            } catch (Exception e) {
                Log.d(LOGTAG, "parsing error");
            }
            return parsingDocument;
        }

        @Override
        protected void onPostExecute(Document document) {

            NodeList nodeList = document.getElementsByTagName("item");
            nearAchievementArrayList.clear();

            for(int i = 0; i< nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                Element firstElement = (Element) node;
                NodeList contentTypeId = firstElement.getElementsByTagName("contenttypeid");
                if(!(contentTypeId.item(0).getChildNodes().item(0).getNodeValue().equals("25"))) {
                    NodeList sumnail = firstElement.getElementsByTagName("firstimage");
                    NodeList mapX = firstElement.getElementsByTagName("mapx");
                    NodeList mapY = firstElement.getElementsByTagName("mapy");
                    NodeList title = firstElement.getElementsByTagName("title");
                    NodeList address = firstElement.getElementsByTagName("addr1");
                    NodeList contentId = firstElement.getElementsByTagName("contentid");
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
                    currentLocation.setLongitude(MyApplication.getCurrentLng());
                    currentLocation.setLatitude(MyApplication.getCurrentLat());
                    try {
                        distance = currentLocation.distanceTo(location);
                    } catch (Exception ex){
                        Log.d(LOGTAG, "current location is not detected");
                    }
                    contentIdString = contentId.item(0).getChildNodes().item(0).getNodeValue();
                    contentTypeIdString = contentTypeId.item(0).getChildNodes().item(0).getNodeValue();
                    nearAchievementArrayList.add(new Achievement(contentIdString, contentTypeIdString, null, titleString, addressString,
                            null, null, "", x, y, sumnailString, distance));
                }
            }
            nearAchievementAdapter.notifyDataSetChanged();
            super.onPostExecute(document);
        }


    }
}
