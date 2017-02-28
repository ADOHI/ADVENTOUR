package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xgc1986.parallaxPagerTransformer.ParallaxPagerTransformer;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "MainActivity";
    private static final int MENU_COLOR_DARK = 0xAF000000;
    private static final int MENU_COLOR_LIGHT = 0xDFFFFFFF;
    private static final float MENU_ALPHA_DARK = 0.5f;
    private static final float MENU_ALPHA_LIGHT = 1.0f;

    private ViewPager backgroundPager;
    private Timer backgroundChangeTimer;
    private int page = 0;
    private int select = 1;

    private ParallaxAdapter backgroundAdapter;
    private ParallaxFragment achievementFrist;
    private ParallaxFragment achievementSecond;
    private ParallaxFragment achievementThird;
    private ParallaxFragment achievementFourth;

    private boolean blurBoolean = false;
    private int width;
    private DatabaseReference appDatabase;

    @BindView(R.id.iv_main_resume)ImageView mainResumeImageView;
    @BindView(R.id.tv_main_profile_big)TextView mainProfileBigTextView;
    @BindView(R.id.tv_main_profile_small)TextView mainProfileSmallTextView;
    @OnClick(R.id.iv_main_resume)void mainResumeClick(View v){
        if(select != 1) {
            select = 1;
            selectMenu(select);
        }
        else {
            Intent intent = new Intent(this, ProfileActivity.class);
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

        appDatabase.child("users").child(MyApplication.getMyUid()
        ).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        setBackground(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error = " + databaseError);
                    }
                });
        blurBoolean = !blurBoolean;
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
        backgroundPager = (ViewPager) findViewById(R.id.pager);
        backgroundPager.setBackgroundColor(0xFF000000);

        ParallaxPagerTransformer parallaxPagerTransformer = new ParallaxPagerTransformer((R.id.kbv_main_image));
        parallaxPagerTransformer.setBorder(10);

        backgroundPager.setPageTransformer(false, parallaxPagerTransformer);

        backgroundAdapter = new ParallaxAdapter(getSupportFragmentManager());
        backgroundAdapter.setPager(backgroundPager);

        achievementFrist = new ParallaxFragment();
        achievementSecond = new ParallaxFragment();
        achievementThird = new ParallaxFragment();
        achievementFourth = new ParallaxFragment();
        backgroundPager.setAdapter(backgroundAdapter);
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
            FixedSpeedScroller scroller = new FixedSpeedScroller(backgroundPager.getContext(), sInterpolator);
            mScroller.set(backgroundPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        backgroundPager.setCurrentItem(0);
        pageSwitcher(5);

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) firstLayout.getLayoutParams();

        layoutParams.width = width;

        firstLayout.setLayoutParams(layoutParams);
        secondLayout.setLayoutParams(layoutParams);
        thirdLayout.setLayoutParams(layoutParams);
        fourthLayout.setLayoutParams(layoutParams);

        appDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void pageSwitcher(int seconds) {
        backgroundChangeTimer = new Timer();
        backgroundChangeTimer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000);
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                public void run() {

                    if (page>3) {
                        backgroundPager.setCurrentItem(0);
                        page = 1;
                    } else{
                        backgroundPager.setCurrentItem(page++);
                    }
                }
            });

        }


    }

    public void showMenu (boolean show){
        Animation ani01 = new AlphaAnimation(0.0f, 1.0f);
        ani01.setDuration(3000);
        mainResumeImageView.setAnimation(ani01);
        mainProfileBigTextView.setAnimation(ani01);
        mainProfileSmallTextView.setAnimation(ani01);
        mainTrophyBigTextView.setAnimation(ani01);
        mainTrophyBigTextView.setAnimation(ani01);
        mainTrophySmallTextView.setAnimation(ani01);
        mainQuestImageView.setAnimation(ani01);
        mainQuestBigTextView.setAnimation(ani01);
        mainQuestSmallTextView.setAnimation(ani01);
        mainFriendImageView.setAnimation(ani01);
        mainFriendBigTextView.setAnimation(ani01);
        mainTrophySmallTextView.setAnimation(ani01);
        if(show) {
            firstLayout.setVisibility(View.VISIBLE);
            secondLayout.setVisibility(View.VISIBLE);
            thirdLayout.setVisibility(View.VISIBLE);
            fourthLayout.setVisibility(View.VISIBLE);
            ani01.start();
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
        mainResumeImageView.setAlpha(MENU_ALPHA_DARK);
        mainProfileBigTextView.setTextColor(MENU_COLOR_DARK);
        mainProfileSmallTextView.setTextColor(MENU_COLOR_DARK);
        mainTrophyImageView.setAlpha(MENU_ALPHA_DARK);
        mainTrophyBigTextView.setTextColor(MENU_COLOR_DARK);
        mainTrophySmallTextView.setTextColor(MENU_COLOR_DARK);
        mainQuestImageView.setAlpha(MENU_ALPHA_DARK);
        mainQuestBigTextView.setTextColor(MENU_COLOR_DARK);
        mainQuestSmallTextView.setTextColor(MENU_COLOR_DARK);
        mainFriendImageView.setAlpha(MENU_ALPHA_DARK);
        mainFriendBigTextView.setTextColor(MENU_COLOR_DARK);
        mainFriendSmallTextView.setTextColor(MENU_COLOR_DARK);
        if(select == 1){
            mainResumeImageView.setAlpha(MENU_ALPHA_LIGHT);
            mainProfileBigTextView.setTextColor(MENU_COLOR_LIGHT);
            mainProfileSmallTextView.setTextColor(MENU_COLOR_LIGHT);
        }
        if(select == 2){
            mainTrophyImageView.setAlpha(MENU_ALPHA_LIGHT);
            mainTrophyBigTextView.setTextColor(MENU_COLOR_LIGHT);
            mainTrophySmallTextView.setTextColor(MENU_COLOR_LIGHT);
        }
        if(select == 3){
            mainQuestImageView.setAlpha(MENU_ALPHA_LIGHT);
            mainQuestBigTextView.setTextColor(MENU_COLOR_LIGHT);
            mainQuestSmallTextView.setTextColor(MENU_COLOR_LIGHT);
        }
        if(select == 4){
            mainFriendImageView.setAlpha(MENU_ALPHA_LIGHT);
            mainFriendBigTextView.setTextColor(MENU_COLOR_LIGHT);
            mainFriendSmallTextView.setTextColor(MENU_COLOR_LIGHT);
        }
    }

    @Override
    protected void onResume() {
        appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        setBackground(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error" + databaseError);
                    }
                });
        super.onResume();
    }

    private void setBackground(User user) {
        try {
            backgroundAdapter.remove(achievementFrist);
            backgroundAdapter.remove(achievementSecond);
            backgroundAdapter.remove(achievementThird);
            backgroundAdapter.remove(achievementFourth);
        } catch (Exception ex) {
            Log.e(LOGTAG, "adapter remove failed");
        }

        Bundle first = new Bundle();
        try {
            first.putString("imageurl", user.getAchievementList().get(0).getImageUrl());
            first.putString("name", user.getAchievementList().get(0).getTitle());
            first.putString("date", user.getAchievementList().get(0).getTime());
        } catch (Exception ex){
            first.putString("imageurl", getString(R.string.default_image));
            first.putString("name", getString(R.string.default_description));
        }
        first.putBoolean("blur", blurBoolean);
        achievementFrist.setArguments(first);

        Bundle second = new Bundle();
        try{
            second.putString("imageurl", user.getAchievementList().get(1).getImageUrl());
            second.putString("name", user.getAchievementList().get(1).getTitle());
            second.putString("date", user.getAchievementList().get(1).getTime());
        }  catch (Exception ex){
            second.putString("imageurl", getString(R.string.default_image));
            second.putString("name", getString(R.string.default_description));
        }

        second.putBoolean("blur", blurBoolean);
        achievementSecond.setArguments(second);

        Bundle third = new Bundle();
        try{
            third.putString("imageurl", user.getAchievementList().get(2).getImageUrl());
            third.putString("name", user.getAchievementList().get(2).getTitle());
            third.putString("date", user.getAchievementList().get(2).getTime());
        }  catch (Exception ex){
            third.putString("imageurl", getString(R.string.default_image));
            third.putString("name", getString(R.string.default_description));
        }

        third.putBoolean("blur", blurBoolean);
        achievementThird.setArguments(third);

        Bundle fourth = new Bundle();
        try{
            fourth.putString("imageurl", user.getAchievementList().get(3).getImageUrl());
            fourth.putString("name", user.getAchievementList().get(3).getTitle());
            fourth.putString("date", user.getAchievementList().get(3).getTime());
        }  catch (Exception ex){
            fourth.putString("imageurl", getString(R.string.default_image));
            fourth.putString("name", getString(R.string.default_description));
        }

        fourth.putBoolean("blur", blurBoolean);
        achievementFourth.setArguments(fourth);

        backgroundAdapter.add(achievementFrist);
        backgroundAdapter.add(achievementSecond);
        backgroundAdapter.add(achievementThird);
        backgroundAdapter.add(achievementFourth);
        if(page!=0) backgroundPager.setCurrentItem(page-1, true);
        backgroundAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
