package com.adostudio.adohi.adventour;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Conquest;
import com.adostudio.adohi.adventour.db.Review;
import com.adostudio.adohi.adventour.db.ReviewAchievement;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.adostudio.adohi.adventour.db.User;
import com.adostudio.adohi.adventour.db.Achievement;
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
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AchievementDetailActivity extends AppCompatActivity {

    private static final String LOGTAG = "AchieveDetailActivity";

    private RecyclerView.Adapter reviewAdapter;
    private RecyclerView.LayoutManager reviewLayoutManager;
    private ArrayList<Review> reviewList;
    @BindView(R.id.rv_review)RecyclerView reviewRecyclerView;
    private Document parsingDocument = null;
    private RequestManager glideRequestManager;
    private DatabaseReference appDatabase;
    private Location bookmarkLocation;
    private String bookmarkContentId;
    private String bookmarkContentTypeId;
    private String bookmarkOverview;
    private String bookmarkTitle;
    private String bookmarkAddress;
    private String bookmarkPhonecall;
    private String bookmarkHomepage;
    private String bookmarkImageUrl;
    private double bookmarkDistance;
    //private long bookmarkTime;

    private String conquestUid = null;
    @BindView(R.id.iv_detail_achievement)ImageView detailAchievementImageView;
    @BindView(R.id.iv_detail_trophy)ImageView detailTrophyImageView;
    @BindView(R.id.tv_detail_title)TextView detailTitleTextView;
    @BindView(R.id.tv_detail_trophy)TextView detailTrophyTextView;
    @BindView(R.id.tv_detail_distance)TextView detailDistanceTextView;
    @BindView(R.id.iv_detail_bookmark)ImageView detailBookmarkImageView;
    @BindView(R.id.tv_detail_intro)TextView introTextView;
    @BindView(R.id.iv_detail_location)ImageView detailLocationImageView;
    @BindView(R.id.tv_detail_location)TextView detailLocationTextView;
    @BindView(R.id.iv_detail_phone_call)ImageView detailPhoneCallImageView;
    @BindView(R.id.tv_detail_phone_call)TextView detailPhoneCallTextView;
    @BindView(R.id.iv_detail_homepage)ImageView detailHomepageImageView;
    @BindView(R.id.tv_detail_homepage)TextView detailHomepageTextView;
    @BindView(R.id.iv_conquest_sumanil)ImageView conquestSumnailImageView;
    @OnClick(R.id.iv_conquest_sumanil)void conquestSumnailClick(){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("uid", conquestUid);
        intent.putExtra("mine", false);
        startActivity(intent);
    }
    @BindView(R.id.tv_conquest_name)TextView conquestNameTextView;
    @OnClick(R.id.iv_detail_bookmark)void BookmarkButtonClick(){
        appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {

                      User user = dataSnapshot.getValue(User.class);
                      Achievement achievement = new Achievement(bookmarkContentId, bookmarkContentTypeId, bookmarkOverview, bookmarkTitle,
                              bookmarkAddress, bookmarkPhonecall, bookmarkHomepage, "", bookmarkLocation.getLongitude(),
                              bookmarkLocation.getLatitude(), bookmarkImageUrl, bookmarkDistance);
                      if(user.checkBookmarkId(bookmarkContentId)){
                          detailBookmarkImageView.setImageResource(R.drawable.bookmark_off);
                          user.removeBookmark(achievement.getContentId());
                          appDatabase.child("users").child(MyApplication.getMyUid()).setValue(user);
                      } else{
                          if(user.getBookmarkList().size() < 3){
                              detailBookmarkImageView.setImageResource(R.drawable.bookmark_on);
                              user.addBookmarkList(achievement);
                              appDatabase.child("users").child(MyApplication.getMyUid()).setValue(user);
                          } else{
                              Toast.makeText(AchievementDetailActivity.this, "북마크 리스트가 가득 찼습니다",
                                      Toast.LENGTH_SHORT).show();
                          }
                      }

                  }

                  @Override
                  public void onCancelled(DatabaseError databaseError) {
                            Log.d(LOGTAG, "database error : " + databaseError);
                        }
                    });
    }

    @BindView(R.id.fab_review)FloatingActionButton reviewAddFAB;
    @OnClick(R.id.fab_review)void reviewAddFABClick(){
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("contentid", bookmarkContentId);
        startActivity(intent);
    }
    @BindView(R.id.tv_achievement_score)TextView scoreTextView;
    @BindView(R.id.tv_review_count)TextView reviewCountTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_detail);
        ButterKnife.bind(this);
        
        appDatabase = FirebaseDatabase.getInstance().getReference();
        glideRequestManager = Glide.with(this);
        Intent intent = getIntent();
        bookmarkContentId = intent.getExtras().getString("contentid");
        bookmarkLocation = new Location(LocationManager.GPS_PROVIDER);
        bookmarkLocation.setLongitude(intent.getExtras().getDouble("mapx"));
        bookmarkLocation.setLatitude(intent.getExtras().getDouble("mapy"));


        bookmarkDistance = intent.getExtras().getDouble("distance");

        if( bookmarkDistance > 1000) {
            String num = String.format("%.2f" , bookmarkDistance/1000);
            detailDistanceTextView.setText(num+"km");
        } else {
            detailDistanceTextView.setText((int)bookmarkDistance+"m");
        }

        StringBuilder urlBuilder = new StringBuilder(getString(R.string.tour_id_search_first));
        urlBuilder.append(getString(R.string.tour_api_key));
        urlBuilder.append(getString(R.string.tour_id_search_second));
        urlBuilder.append(intent.getExtras().getString("contentid"));
        urlBuilder.append(getString(R.string.tour_id_search_third));
        GetXMLTask getXMLTask= new GetXMLTask();
        getXMLTask.execute(urlBuilder.toString());
        Log.d(LOGTAG, urlBuilder.toString());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("loaction");

        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLongitude(intent.getExtras().getDouble("mapx"));
                location.setLatitude(intent.getExtras().getDouble("mapy"));

                if( location.distanceTo(bookmarkLocation) > 1000) {
                    String num = String.format("%.2f" , (location.distanceTo(bookmarkLocation))/1000);
                    detailDistanceTextView.setText(num+"km");
                } else {
                    detailDistanceTextView.setText((int)location.distanceTo(bookmarkLocation) + "m");
                }
            }

        };
        registerReceiver(receiver,intentFilter);




        reviewList = new ArrayList<>();
        reviewRecyclerView.setHasFixedSize(true);
        reviewLayoutManager = new LinearLayoutManager(this);
        reviewRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewAdapter = new ReviewAdapter(this, reviewList, glideRequestManager);
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

    }
    private class GetXMLTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                parsingDocument = documentBuilder.parse(new InputSource(url.openStream()));
                parsingDocument.getDocumentElement().normalize();

            } catch (Exception ex) {
                Log.d(LOGTAG, "parsing error");
            }
            return parsingDocument;
        }

        @Override
        protected void onPostExecute(Document document) {

            NodeList nodeList = document.getElementsByTagName("item");
            bookmarkContentTypeId = null;
            bookmarkOverview = null;
            bookmarkTitle = null;
            bookmarkAddress = null;
            bookmarkPhonecall = null;
            bookmarkHomepage = null;
            bookmarkImageUrl = null;
            for(int i = 0; i< nodeList.getLength(); i++){

                Node node = nodeList.item(i);
                Element firstElement = (Element) node;

                NodeList contentTypeId = firstElement.getElementsByTagName("contenttypeid");
                bookmarkContentTypeId = contentTypeId.item(0).getChildNodes().item(0).getNodeValue();

                NodeList title = firstElement.getElementsByTagName("title");
                bookmarkTitle = title.item(0).getChildNodes().item(0).getNodeValue();
                detailTitleTextView.setText(bookmarkTitle);

                NodeList overView = firstElement.getElementsByTagName("overview");
                bookmarkOverview = (overView.item(0).getChildNodes().item(0).getNodeValue().replaceAll("<br>", "").replaceAll("<br />", "").replaceAll("<br/>", ""));
                introTextView.setText(bookmarkOverview);

                try {
                    NodeList address = firstElement.getElementsByTagName("addr1");
                    bookmarkAddress = address.item(0).getChildNodes().item(0).getNodeValue();
                    detailLocationTextView.setText(bookmarkAddress);
                    detailLocationImageView.setImageResource(R.drawable.location);
                } catch (Exception ex){
                    detailLocationTextView.setHeight(0);
                }

                try {
                    NodeList tel = firstElement.getElementsByTagName("tel");
                    bookmarkPhonecall = tel.item(0).getChildNodes().item(0).getNodeValue();
                    detailPhoneCallTextView.setText(bookmarkPhonecall);
                    detailPhoneCallImageView.setImageResource(R.drawable.phone_call);
                } catch (Exception ex){
                    detailPhoneCallTextView.setHeight(0);
                }

                try {
                    NodeList homepage = firstElement.getElementsByTagName("homepage");
                    String homepageString = homepage.item(0).getChildNodes().item(0).getNodeValue();
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"(.+?)\"");
                    java.util.regex.Matcher matcher = pattern.matcher(homepageString);
                    if(matcher.find()) {
                        homepageString = matcher.group(1);
                    }
                    bookmarkHomepage = homepageString;
                    detailHomepageTextView.setText(homepageString);
                    detailHomepageImageView.setImageResource(R.drawable.homepage);
                } catch (Exception ex){
                    detailHomepageTextView.setHeight(0);
                }

                try{
                    NodeList sumnail = firstElement.getElementsByTagName("firstimage");
                    bookmarkImageUrl = sumnail.item(0).getChildNodes().item(0).getNodeValue();
                    glideRequestManager.load(bookmarkImageUrl)
                            .thumbnail(0.1f)
                            .into(detailAchievementImageView);
                }catch (Exception ex){
                    glideRequestManager.load(R.drawable.no_image)
                            .thumbnail(0.1f)
                            .into(detailAchievementImageView);
                }

            }
            super.onPostExecute(document);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Achievement achievement = new Achievement(bookmarkContentId, bookmarkContentTypeId, bookmarkOverview, bookmarkTitle,
                                bookmarkAddress, bookmarkPhonecall, bookmarkHomepage, "", bookmarkLocation.getLongitude(),
                                bookmarkLocation.getLatitude(), bookmarkImageUrl, bookmarkDistance);

                        if(user.checkBookmarkId(bookmarkContentId)){
                            detailBookmarkImageView.setImageResource(R.drawable.bookmark_on);
                        } else {
                            detailBookmarkImageView.setImageResource(R.drawable.bookmark_off);
                        }
                        if(user.checkAchievementId(bookmarkContentId)){
                            detailTrophyImageView.setImageResource(R.drawable.trophy_on);
                            detailTrophyTextView.setText("획득");
                            reviewAddFAB.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error : " + databaseError);
                    }
                });

        appDatabase.child("conquests").child(bookmarkContentId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            Conquest conquest = dataSnapshot.getValue(Conquest.class);
                            conquestNameTextView.setText(conquest.getName());
                            glideRequestManager.load(conquest.getImageUrl())
                                    .into(conquestSumnailImageView);
                            conquestUid = conquest.getUid();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error : " + databaseError);
                    }
                });

        appDatabase.child("reviews").child(bookmarkContentId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        if(dataSnapshot.exists()) {
                            reviewList.clear();
                            ReviewAchievement reviewAchievement = dataSnapshot.getValue(ReviewAchievement.class);
                            reviewList.addAll(reviewAchievement.getReviews());
                            reviewAdapter.notifyDataSetChanged();
                            String score = String.format("%.1f" , reviewAchievement.getStars());
                            scoreTextView.setText(score);
                            reviewCountTextView.setText(reviewList.size()+"");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error = " + databaseError);
                    }
                });
    }
}
