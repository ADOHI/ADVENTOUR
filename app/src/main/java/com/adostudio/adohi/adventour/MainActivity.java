package com.adostudio.adohi.adventour;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adostudio.adohi.adventour.db.Achievement;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xgc1986.parallaxPagerTransformer.ParallaxPagerTransformer;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    ViewPager mPager;
    Timer timer;
    int page = 0;
    int maxPage = 0;
    private int select = 1;
    boolean pageCheck = false;

    private DemoParallaxAdapter mAdapter;
    private DemoParallaxFragment achievementFrist;
    private DemoParallaxFragment achievementSecond;
    private DemoParallaxFragment achievementThird;
    private DemoParallaxFragment achievementFourth;
    private DemoParallaxFragment achievementEmpty;
    private boolean blurBoolean = false;
    private int width;
    private DatabaseReference mDatabase;
    private String uid;
    @BindView(R.id.iv_main_resume)ImageView mainResumeImageView;
    @BindView(R.id.tv_main_profile_big)TextView mainProfileBigTextView;
    @BindView(R.id.tv_main_profile_small)TextView mainProfileSmallTextView;
    @OnClick(R.id.iv_main_resume)void mainResumeClick(){
        if(select != 1) {
            select = 1;
            selectMenu(select);
        }
        else {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("uid", uid);
            intent.putExtra("mine", true);
            intent.putExtra("add", 0);
            startActivity(intent);
        }
    }

    @BindView(R.id.iv_main_trophy)ImageView mainTrophyImageView;
    @BindView(R.id.tv_main_trophy_big)TextView mainTrophyBigTextView;
    @BindView(R.id.tv_main_trophy_small)TextView mainTrophySmallTextView;
    @OnClick(R.id.iv_main_trophy)void mainTrophyClick(){
        if(select != 2) {
            select = 2;
            selectMenu(select);
        }
        else {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
    }

    @BindView(R.id.iv_main_quest)ImageView mainQuestImageView;
    @BindView(R.id.tv_main_quest_big)TextView mainQuestBigTextView;
    @BindView(R.id.tv_main_quest_small)TextView mainQuestSmallTextView;
    @OnClick(R.id.iv_main_quest)void mainQuestClick(){
        if(select != 3) {
            select = 3;
            selectMenu(select);
        }
        else {
            Intent intent = new Intent(this, QuestActivity.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        }
    }

    @BindView(R.id.iv_main_friend)ImageView mainFriendImageView;
    @BindView(R.id.tv_main_friend_big)TextView mainFriendBigTextView;
    @BindView(R.id.tv_main_friend_small)TextView mainFriendSmallTextView;
    @OnClick(R.id.iv_main_friend)void mainFriendClick(){
        if(select != 4) {
            select = 4;
            selectMenu(select);
        }
        else {
            Intent intent = new Intent(this, FriendActivity.class);
            intent.putExtra("issue", false);
            startActivity(intent);
        }
    }

    @BindView(R.id.fb_main_menu)FloatingActionButton mainMenuFloatingActionButton;
    @OnClick(R.id.fb_main_menu)void mainMenuClick(){

        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mAdapter.remove(achievementFrist);
                        mAdapter.remove(achievementSecond);
                        mAdapter.remove(achievementThird);
                        mAdapter.remove(achievementFourth);


                        User user = dataSnapshot.getValue(User.class);

                        Bundle first = new Bundle();
                        try {
                            first.putString("imageurl", user.achievementList.get(0).imageUrl);
                            first.putString("name", user.achievementList.get(0).title);
                            first.putString("date", user.achievementList.get(0).time);
                        } catch (Exception ex){
                            first.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            first.putString("name", "지금 당장 모험을 떠나세요");
                        }
                        first.putBoolean("blur", blurBoolean);
                        achievementFrist.setArguments(first);

                        Bundle second = new Bundle();
                        try{
                            second.putString("imageurl", user.achievementList.get(1).imageUrl);
                            second.putString("name", user.achievementList.get(1).title);
                            second.putString("date", user.achievementList.get(1).time);
                        }  catch (Exception ex){
                            second.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            second.putString("name", "지금 당장 모험을 떠나세요");
                        }

                        second.putBoolean("blur", blurBoolean);
                        achievementSecond.setArguments(second);

                        Bundle third = new Bundle();
                        try{
                            third.putString("imageurl", user.achievementList.get(2).imageUrl);
                            third.putString("name", user.achievementList.get(2).title);
                            third.putString("date", user.achievementList.get(2).time);
                        }  catch (Exception ex){
                            third.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            third.putString("name", "지금 당장 모험을 떠나세요");
                        }

                        third.putBoolean("blur", blurBoolean);
                        achievementThird.setArguments(third);

                        Bundle fourth = new Bundle();
                        try{
                            fourth.putString("imageurl", user.achievementList.get(3).imageUrl);
                            fourth.putString("name", user.achievementList.get(3).title);
                            fourth.putString("date", user.achievementList.get(3).time);
                        }  catch (Exception ex){
                            fourth.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            fourth.putString("name", "지금 당장 모험을 떠나세요");
                        }

                        fourth.putBoolean("blur", blurBoolean);
                        achievementFourth.setArguments(fourth);
                        mAdapter.add(achievementFrist);
                        mAdapter.add(achievementSecond);
                        mAdapter.add(achievementThird);
                        mAdapter.add(achievementFourth);
                        if(page!=0)mPager.setCurrentItem(page-1, true);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        blurBoolean = !blurBoolean;
        //setBlur(blurBoolean);
        showMenu(blurBoolean);
    }

    @BindView(R.id.ll_main_first)LinearLayout firstLayout;
    @BindView(R.id.ll_main_second)LinearLayout secondLayout;
    @BindView(R.id.ll_main_third)LinearLayout thirdLayout;
    @BindView(R.id.ll_main_fourth)LinearLayout fourthLayout;

    @BindView(R.id.iv_ani)ImageView aniImageView;

    private GoogleApiClient mGoogleApiClient;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setBackgroundColor(0xFF000000);

        ParallaxPagerTransformer pt = new ParallaxPagerTransformer((R.id.kbv_main_image));
        pt.setBorder(10);

        mPager.setPageTransformer(false, pt);

        mAdapter = new DemoParallaxAdapter(getSupportFragmentManager());
        mAdapter.setPager(mPager); //only for this transformer

        achievementFrist = new DemoParallaxFragment();
        achievementSecond = new DemoParallaxFragment();
        achievementThird = new DemoParallaxFragment();
        achievementFourth = new DemoParallaxFragment();
        mPager.setAdapter(mAdapter);
        showMenu(blurBoolean);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().show();
        }
        Interpolator sInterpolator = new AccelerateInterpolator();
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mPager.getContext(), sInterpolator);
            mScroller.set(mPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        mPager.setCurrentItem(0);
        pageSwitcher(5);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        width = dm.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) firstLayout.getLayoutParams();

        lp.width = width;

        firstLayout.setLayoutParams(lp);
        secondLayout.setLayoutParams(lp);
        thirdLayout.setLayoutParams(lp);
        fourthLayout.setLayoutParams(lp);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(uid).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mAdapter.remove(achievementFrist);
                        mAdapter.remove(achievementSecond);
                        mAdapter.remove(achievementThird);
                        mAdapter.remove(achievementFourth);
                        User user = dataSnapshot.getValue(User.class);

                        Bundle first = new Bundle();
                        try {
                            first.putString("imageurl", user.achievementList.get(0).imageUrl);
                            first.putString("name", user.achievementList.get(0).title);
                            first.putString("date", user.achievementList.get(0).time);
                        } catch (Exception ex){
                            first.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            first.putString("name", "지금 당장 모험을 떠나세요");
                         }
                        first.putBoolean("blur", blurBoolean);
                        achievementFrist.setArguments(first);

                        Bundle second = new Bundle();
                        try{
                            second.putString("imageurl", user.achievementList.get(1).imageUrl);
                            second.putString("name", user.achievementList.get(1).title);
                            second.putString("date", user.achievementList.get(1).time);
                        }  catch (Exception ex){
                            second.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            second.putString("name", "지금 당장 모험을 떠나세요");
                        }

                        second.putBoolean("blur", blurBoolean);
                        achievementSecond.setArguments(second);

                        Bundle third = new Bundle();
                        try{
                            third.putString("imageurl", user.achievementList.get(2).imageUrl);
                            third.putString("name", user.achievementList.get(2).title);
                            third.putString("date", user.achievementList.get(2).time);
                        }  catch (Exception ex){
                            third.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            third.putString("name", "지금 당장 모험을 떠나세요");
                        }

                        third.putBoolean("blur", blurBoolean);
                        achievementThird.setArguments(third);

                        Bundle fourth = new Bundle();
                        try{
                            fourth.putString("imageurl", user.achievementList.get(3).imageUrl);
                            fourth.putString("name", user.achievementList.get(3).title);
                            fourth.putString("date", user.achievementList.get(3).time);
                        }  catch (Exception ex){
                            fourth.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            fourth.putString("name", "지금 당장 모험을 떠나세요");
                        }

                        second.putBoolean("blur", blurBoolean);
                        achievementFourth.setArguments(fourth);
                        mAdapter.add(achievementFrist);
                        mAdapter.add(achievementSecond);
                        mAdapter.add(achievementThird);
                        mAdapter.add(achievementFourth);
                        if(page!=0)mPager.setCurrentItem(page-1, true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
        // in
        // milliseconds
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // this is an inner class...
    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {

                    if (page>3) { // In my case the number of pages are 5
                        // Showing a toast for just testing purpose

                        mPager.setCurrentItem(0);
                        page = 1;
                    } else{
                        mPager.setCurrentItem(page++);
                    }
                }
            });

        }
    }

    public void showMenu (boolean show){
        Animation ani01 = new AlphaAnimation(0.0f, 1.0f);
        ani01.setDuration(3000);
        mainResumeImageView.setAnimation(ani01);
        if(show) {
            firstLayout.setVisibility(View.VISIBLE);
            ani01.start();
            secondLayout.setVisibility(View.VISIBLE);
            thirdLayout.setVisibility(View.VISIBLE);
            fourthLayout.setVisibility(View.VISIBLE);
        } else {
            firstLayout.setVisibility(View.GONE);
            secondLayout.setVisibility(View.GONE);
            thirdLayout.setVisibility(View.GONE);
            fourthLayout.setVisibility(View.GONE);
        }

        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(2000);
        animation.setFillAfter(true);
        aniImageView.setAnimation(animation);
        animation.start();
    }

    public void selectMenu (int select){
        int blackColor = 0xAF000000;
        int whiteColor = 0xDFFFFFFF;
        float blackAlpha = 0.5f;
        float whiteAlpha = 1.0f;
        mainResumeImageView.setAlpha(blackAlpha);
        mainProfileBigTextView.setTextColor(blackColor);
        mainProfileSmallTextView.setTextColor(blackColor);
        mainTrophyImageView.setAlpha(blackAlpha);
        mainTrophyBigTextView.setTextColor(blackColor);
        mainTrophySmallTextView.setTextColor(blackColor);
        mainQuestImageView.setAlpha(blackAlpha);
        mainQuestBigTextView.setTextColor(blackColor);
        mainQuestSmallTextView.setTextColor(blackColor);
        mainFriendImageView.setAlpha(blackAlpha);
        mainFriendBigTextView.setTextColor(blackColor);
        mainFriendSmallTextView.setTextColor(blackColor);
        if(select == 1){
            mainResumeImageView.setAlpha(whiteAlpha);
            mainProfileBigTextView.setTextColor(whiteColor);
            mainProfileSmallTextView.setTextColor(whiteColor);
        }
        if(select == 2){
            mainTrophyImageView.setAlpha(whiteAlpha);
            mainTrophyBigTextView.setTextColor(whiteColor);
            mainTrophySmallTextView.setTextColor(whiteColor);
        }
        if(select == 3){
            mainQuestImageView.setAlpha(whiteAlpha);
            mainQuestBigTextView.setTextColor(whiteColor);
            mainQuestSmallTextView.setTextColor(whiteColor);
        }
        if(select == 4){
            mainFriendImageView.setAlpha(whiteAlpha);
            mainFriendBigTextView.setTextColor(whiteColor);
            mainFriendSmallTextView.setTextColor(whiteColor);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDatabase.child("users").child(uid).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mAdapter.remove(achievementFrist);
                        mAdapter.remove(achievementSecond);
                        mAdapter.remove(achievementThird);
                        mAdapter.remove(achievementFourth);
                        User user = dataSnapshot.getValue(User.class);

                        Bundle first = new Bundle();
                        try {
                            first.putString("imageurl", user.achievementList.get(0).imageUrl);
                            first.putString("name", user.achievementList.get(0).title);
                            first.putString("date", user.achievementList.get(0).time);
                        } catch (Exception ex){
                            first.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            first.putString("name", "지금 당장 모험을 떠나세요");
                        }
                        first.putBoolean("blur", blurBoolean);
                        achievementFrist.setArguments(first);

                        Bundle second = new Bundle();
                        try{
                            second.putString("imageurl", user.achievementList.get(1).imageUrl);
                            second.putString("name", user.achievementList.get(1).title);
                            second.putString("date", user.achievementList.get(1).time);
                        }  catch (Exception ex){
                            second.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            second.putString("name", "지금 당장 모험을 떠나세요");
                        }

                        second.putBoolean("blur", blurBoolean);
                        achievementSecond.setArguments(second);

                        Bundle third = new Bundle();
                        try{
                            third.putString("imageurl", user.achievementList.get(2).imageUrl);
                            third.putString("name", user.achievementList.get(2).title);
                            third.putString("date", user.achievementList.get(2).time);
                        }  catch (Exception ex){
                            third.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            third.putString("name", "지금 당장 모험을 떠나세요");
                        }

                        third.putBoolean("blur", blurBoolean);
                        achievementThird.setArguments(third);

                        Bundle fourth = new Bundle();
                        try{
                            fourth.putString("imageurl", user.achievementList.get(3).imageUrl);
                            fourth.putString("name", user.achievementList.get(3).title);
                            fourth.putString("date", user.achievementList.get(3).time);
                        }  catch (Exception ex){
                            fourth.putString("imageurl", "https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg");
                            fourth.putString("name", "지금 당장 모험을 떠나세요");
                        }

                        second.putBoolean("blur", blurBoolean);
                        achievementFourth.setArguments(fourth);
                        mAdapter.add(achievementFrist);
                        mAdapter.add(achievementSecond);
                        mAdapter.add(achievementThird);
                        mAdapter.add(achievementFourth);
                        if(page!=0)mPager.setCurrentItem(page-1, true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        blurBoolean = false;
        showMenu(blurBoolean);

    }
}