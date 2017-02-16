package com.example.adohi.adventour;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.adohi.adventour.db.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhn.android.maps.maplib.NGeoPoint;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AchievementDetailActivity extends AppCompatActivity {

    private Document doc = null;
    private RequestManager mGlideRequestManager;
    private DatabaseReference mDatabase;
    private NGeoPoint bookmarkNGeoPoint;
    private String bookmarkId;
    private String uid;
    @BindView(R.id.iv_detail_achievement)ImageView detailAchievementImageView;
    @BindView(R.id.tv_detail_title)TextView detailTitleTextView;
    @BindView(R.id.tv_detail_distance)TextView detailDistanceTextView;
    @BindView(R.id.iv_detail_bookmark)ImageView detailBookmarkImageView;
    @BindView(R.id.tv_detail_intro)TextView introTextView;
    @BindView(R.id.iv_detail_location)ImageView detailLocationImageView;
    @BindView(R.id.tv_detail_location)TextView detailLocationTextView;
    @BindView(R.id.iv_detail_phone_call)ImageView detailPhoneCallImageView;
    @BindView(R.id.tv_detail_phone_call)TextView detailPhoneCallTextView;
    @BindView(R.id.iv_detail_homepage)ImageView detailHomepageImageView;
    @BindView(R.id.tv_detail_homepage)TextView detailHomepageTextView;

    @OnClick(R.id.iv_detail_bookmark)void BookmarkButtonClick(){
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                      User user = dataSnapshot.getValue(User.class);
                      Log.d("testttt", "" + user.bookmarkIdList.size());
                      if(user.bookmarkIdList.contains(bookmarkId)){
                          detailBookmarkImageView.setImageResource(R.drawable.bookmark_off);
                          user.bookmarkIdList.remove(bookmarkId);
                          mDatabase.child("users").child(uid).child("bookmarkIdList").setValue(user.bookmarkIdList);
                      } else{
                          if(user.bookmarkIdList.size() < 3){
                              detailBookmarkImageView.setImageResource(R.drawable.bookmark_on);
                              user.bookmarkIdList.add(bookmarkId);
                              mDatabase.child("users").child(uid).child("bookmarkIdList").setValue(user.bookmarkIdList);
                          } else{
                              Toast.makeText(AchievementDetailActivity.this, "북마크 리스트가 가득 찼습니다",
                                      Toast.LENGTH_SHORT).show();
                          }
                      }

                        // ...
                  }

                  @Override
                  public void onCancelled(DatabaseError databaseError) {

                      }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_detail);
        ButterKnife.bind(this);

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
        mGlideRequestManager = Glide.with(this);
        Intent intent = getIntent();
        bookmarkId = intent.getExtras().getString("contentid");
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        Log.d("testttt", "" + user.bookmarkIdList.size());
                        if(user.bookmarkIdList.contains(bookmarkId)){
                            detailBookmarkImageView.setImageResource(R.drawable.bookmark_on);
                        }
                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        detailDistanceTextView.setText(intent.getExtras().getString("distance"));
        StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon");
        urlBuilder.append("?" + "ServiceKey" + "=JNbqf4NQaSTqFcIueZ7tna%2B1OvOKTiRGmCMpdoL%2FxlE4YUkZPfVhxk9rwarKYNACs1UfYGj49jO%2BKFYKSqsFhQ%3D%3D");
        urlBuilder.append("&contentTypeId=");
        urlBuilder.append("&contentId" + "=" + intent.getExtras().getString("contentid"));
        urlBuilder.append("&MobileOS=AND&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y");
        GetXMLTask getXMLTask= new GetXMLTask();
        getXMLTask.execute(urlBuilder.toString());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("loaction");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("whyyyyy", "dhoooo");
                Intent activityStartIntent = new Intent(context, MainActivity.class);
                activityStartIntent.putExtra("y", intent.getExtras().getDouble("y"));
                activityStartIntent.putExtra("x", intent.getExtras().getDouble("x"));
                context.startActivity(activityStartIntent);
                overridePendingTransition(R.anim.push_left_in, R.anim.hold);
                finish();
            }

        };
        registerReceiver(receiver,intentFilter);

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
            for(int i = 0; i< nodeList.getLength(); i++){

                Node node = nodeList.item(i); //data엘리먼트 노드
                Element fstElmnt = (Element) node;

                NodeList title = fstElmnt.getElementsByTagName("title");
                detailTitleTextView.setText(title.item(0).getChildNodes().item(0).getNodeValue());
                NodeList overView = fstElmnt.getElementsByTagName("overview");
                introTextView.setText((overView.item(0).getChildNodes().item(0).getNodeValue().replaceAll("<br>", "").replaceAll("<br />", "\n")));

                try {
                    NodeList address = fstElmnt.getElementsByTagName("addr1");
                    detailLocationTextView.setText(address.item(0).getChildNodes().item(0).getNodeValue());
                    detailLocationImageView.setImageResource(R.drawable.location);
                } catch (Exception ex){
                    detailLocationTextView.setHeight(0);
                }

                try {
                    NodeList tel = fstElmnt.getElementsByTagName("tel");
                    detailPhoneCallTextView.setText(tel.item(0).getChildNodes().item(0).getNodeValue());
                    detailPhoneCallImageView.setImageResource(R.drawable.phone_call);
                } catch (Exception ex){
                    detailPhoneCallTextView.setHeight(0);
                }

                try {
                    NodeList homepage = fstElmnt.getElementsByTagName("homepage");
                    String homepageString = homepage.item(0).getChildNodes().item(0).getNodeValue();
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"(.+?)\"");
                    java.util.regex.Matcher matcher = pattern.matcher(homepageString);
                    if(matcher.find()) {
                        homepageString = matcher.group(1);
                    }
                    detailHomepageTextView.setText(homepageString);
                    detailHomepageImageView.setImageResource(R.drawable.homepage);
                } catch (Exception ex){
                    detailHomepageTextView.setHeight(0);
                }

                try{
                    NodeList sumnail = fstElmnt.getElementsByTagName("firstimage");
                    mGlideRequestManager.load(sumnail.item(0).getChildNodes().item(0).getNodeValue()).into(detailAchievementImageView);
                    //holder.mainRowImageView.set
                }catch (Exception ex){

                }

                try {
                } catch (Exception ex) {
                    Log.d("isok", "imagenull");
                }

            }
            super.onPostExecute(doc);
        }


    }//end inner class - GetXMLTask

}
