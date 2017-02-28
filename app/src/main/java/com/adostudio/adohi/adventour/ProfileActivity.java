package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Achievement;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String LOGTAG = "ProfileActivity";

    private String friendUid;
    private String memo = null;
    private int add;
    private boolean mine;
    private String uid;
    private RequestManager glideRequestManager;
    private DatabaseReference appDatabase;
    private RecyclerView.Adapter latelyAchievementAdapter;
    private RecyclerView.LayoutManager latelyAchievementLayoutManager;
    private ArrayList<Achievement> latelyAchievementList;
    private int trophyRanking;
    private int trophySame;
    private int flagRanking;
    private int flagSame;
    private int userTrophyScore;
    private int userFlagScore;
    @BindView(R.id.sv_profile)ScrollView profileScrollView;
    @BindView(R.id.rv_profile_achievement)RecyclerView latelyAchievementRecyclerView;
    @BindView(R.id.tv_profile_name)TextView profileNameTextView;
    @BindView(R.id.iv_profile_picture)ImageView profilePictureImageView;
    @BindView(R.id.iv_profile_trophy)CircleImageView profileTrophyImageView;
    @BindView(R.id.iv_profile_flag)CircleImageView profileFlagImageView;
    @BindView(R.id.iv_profile_last)ImageView profileLastImageView;
    @BindView(R.id.tv_profile_memo)TextView profileMemoTextView;
    @BindView(R.id.tv_modify_memo)TextView modifyMemoTextView;
    @BindView(R.id.tv_profile_trophy_score)TextView profileTrophyScoreTextView;
    @BindView(R.id.tv_profile_flag_score)TextView profileFlagScoreTextView;
    @BindView(R.id.tv_profile_trophy_ranking)TextView profileTrophyRankingTextView;
    @BindView(R.id.tv_profile_flag_ranking)TextView profileFlagRankingTextView;
    @OnClick(R.id.iv_profile_flag)void profileFlagClick() {
        Intent intent = new Intent(this, FlagActivity.class);
        intent.putExtra("trophy_button", false);
        intent.putExtra("mine", false);
        if(!mine)intent.putExtra("frienduid", friendUid);
        startActivity(intent);
    }
    @OnClick(R.id.iv_profile_trophy)void profileTrophyClick() {
        Intent intent = new Intent(this, FlagActivity.class);
        intent.putExtra("trophy_button", true);
        intent.putExtra("mine", false);
        if(!mine)intent.putExtra("frienduid", friendUid);
        startActivity(intent);
    }
    @BindView(R.id.ll_profile_memo)LinearLayout profileMemoLinearLayout;
    @OnClick(R.id.ll_profile_memo)void memoModifyClick() {
        if(mine) {
            Intent intent = new Intent(this, MemoModifyActivity.class);
            intent.putExtra("uid", MyApplication.getMyUid());
            startActivity(intent);
        }
    }
    @BindView(R.id.fab_profile_add)FloatingActionButton addFAB;
    @OnClick(R.id.fab_profile_add)void addFABClick(){

        appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);
                        user.addFriendList(friendUid);
                        appDatabase.child("users").child(MyApplication.getMyUid()).child("friendList").setValue(user.getFriendList());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error = " + databaseError);
                    }
                });
        addFAB.setVisibility(View.GONE);
        deleteFAB.setVisibility(View.VISIBLE);
    }
    @BindView(R.id.fab_profile_delete)FloatingActionButton deleteFAB;
    @OnClick(R.id.fab_profile_delete)void deleteFABClick(){

        appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        user.removeFriendList(friendUid);
                        appDatabase.child("users").child(MyApplication.getMyUid()).child("friendList").setValue(user.getFriendList());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error : " + databaseError);

                    }
                });
        deleteFAB.setVisibility(View.GONE);
        addFAB.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        friendUid = intent.getExtras().getString("frienduid");
        mine = intent.getExtras().getBoolean("mine");
        add = intent.getExtras().getInt("add");
        if(add == 1) addFAB.setVisibility(View.VISIBLE);
        else if(add == 2) deleteFAB.setVisibility(View.VISIBLE);
        if(!mine) modifyMemoTextView.setVisibility(View.GONE);
        glideRequestManager = Glide.with(this);

        appDatabase = FirebaseDatabase.getInstance().getReference();


        if(mine)uid = MyApplication.getMyUid();
        else uid = friendUid;

        appDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        latelyAchievementList.clear();
                        User user = dataSnapshot.getValue(User.class);

                        userTrophyScore = user.getAchievementList().size();
                        userFlagScore = user.getFlagList().size();
                        profileNameTextView.setText(user.getUserName());
                        glideRequestManager.load(user.getPhotoUrl())
                                .into(profilePictureImageView);
                        try{
                            glideRequestManager.load(user.getAchievementList().get(0).getImageUrl())
                                    .thumbnail(0.1f)
                                    .into(profileLastImageView);
                        } catch (Exception ex) {
                            if(user.getAchievementList().size() == 0)glideRequestManager
                                    .load(getString(R.string.default_image))
                                    .thumbnail(0.1f)
                                    .into(profileLastImageView);
                            else glideRequestManager.load(R.drawable.no_image).into(profileLastImageView);
                        }
                        if(!user.getMemo().equals("")) {
                            memo = user.getMemo();
                            profileMemoTextView.setText(memo);
                        } else profileMemoTextView.setText(getString(R.string.intro_default));
                        profileTrophyScoreTextView.setText(user.getAchievementList().size() + "");
                        profileFlagScoreTextView.setText(user.getFlagList().size() + "");

                        latelyAchievementList.addAll(user.getAchievementList());
                        latelyAchievementAdapter.notifyDataSetChanged();
                        profileScrollView.scrollTo(0,0);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error : " + databaseError);

                    }
                });



        appDatabase.child("users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        trophyRanking = 1;
                        flagRanking = 1;
                        trophySame = 1;
                        flagSame = 1;
                        Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                        while(iter.hasNext()){
                            User user =  iter.next().getValue(User.class);
                            if(!user.getUid().equals(MyApplication.getMyUid())) {
                                if(userTrophyScore < user.getAchievementList().size()) {
                                    trophyRanking += trophySame;
                                    trophySame = 1;
                                } else if (userTrophyScore == user.getAchievementList().size()) {
                                    trophySame += 1;
                                }

                                if(userFlagScore < user.getFlagList().size()) {
                                    flagRanking += flagSame;
                                    flagSame = 1;
                                } else if (userFlagScore == user.getAchievementList().size()) {
                                    flagSame += 1;
                                }
                            }
                        }
                        profileTrophyRankingTextView.setText(trophyRanking+"등");
                        profileFlagRankingTextView.setText(flagRanking+"등");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        latelyAchievementList = new ArrayList<>();
        glideRequestManager = Glide.with(this);
        latelyAchievementRecyclerView.setHasFixedSize(true);
        latelyAchievementLayoutManager = new LinearLayoutManager(this);
        latelyAchievementRecyclerView.setLayoutManager(latelyAchievementLayoutManager);
        latelyAchievementAdapter = new AchievementAdapter(this, latelyAchievementList, glideRequestManager);
        latelyAchievementRecyclerView.setAdapter(latelyAchievementAdapter);
        latelyAchievementRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        profileScrollView.scrollTo(0,profileScrollView.getTop());

        ActionBar ab = getSupportActionBar();
        ab.setTitle("프로필");

    }

    @Override
    protected void onResume() {
        super.onResume();
        appDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);
                        if(!user.getMemo().equals("")) {
                            memo = user.getMemo();
                            profileMemoTextView.setText(memo);
                        } else profileMemoTextView.setText(getString(R.string.intro_default));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error : " + databaseError);

                    }
                });


    }
}
