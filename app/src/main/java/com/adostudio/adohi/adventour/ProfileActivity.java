package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.db.Achievement;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {
    private String uid;
    private String myUid;
    private String memo = null;
    private int add;
    private boolean mine;
    private RequestManager mGlideRequestManager;
    private DatabaseReference mDatabase;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Achievement> latelyAchievementList;
    @BindView(R.id.sv_profile)ScrollView profileScrollView;
    @BindView(R.id.rv_profile_achievement)RecyclerView mRecyclerView;
    @BindView(R.id.tv_profile_name)TextView profileNameTextView;
    @BindView(R.id.iv_profile_picture)ImageView profilePictureImageView;
    @BindView(R.id.iv_profile_trophy)ImageView profileTrophyImageView;
    @BindView(R.id.iv_profile_flag)ImageView profileFlagImageView;
    @BindView(R.id.iv_profile_last)ImageView profileLastImageView;
    @BindView(R.id.tv_profile_memo)TextView profileMemoTextView;
    @BindView(R.id.tv_modify_memo)TextView modifyMemoTextView;
    @BindView(R.id.tv_profile_score)TextView profileScoreTextView;
    @BindView(R.id.tv_profile_flag)TextView profileFlagTextView;
    @OnClick(R.id.iv_profile_flag)void profileFlagClick() {
        Intent intent = new Intent(this, FlagActivity.class);
        intent.putExtra("trophy_button", false);
        startActivity(intent);
    }
    @OnClick(R.id.iv_profile_trophy)void profileTrophyClick() {
        Intent intent = new Intent(this, FlagActivity.class);
        intent.putExtra("trophy_button", true);
        startActivity(intent);
    }
    @BindView(R.id.ll_profile_memo)LinearLayout profileMemoLinearLayout;
    @OnClick(R.id.ll_profile_memo)void memoModifyClick() {
        if(mine) {
            Intent intent = new Intent(this, MemoModifyActivity.class);
            intent.putExtra("memo", memo);
            intent.putExtra("uid", uid);
            intent.putExtra("mine", mine);
            startActivity(intent);
        }
    }
    @BindView(R.id.fab_profile_add)FloatingActionButton addFAB;
    @OnClick(R.id.fab_profile_add)void addFABClick(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        }
        mDatabase.child("users").child(myUid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        user.friendList.add(uid);
                        mDatabase.child("users").child(myUid).setValue(user);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        addFAB.setVisibility(View.GONE);
        deleteFAB.setVisibility(View.VISIBLE);
    }
    @BindView(R.id.fab_profile_delete)FloatingActionButton deleteFAB;
    @OnClick(R.id.fab_profile_delete)void deleteFABClick(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        }
        mDatabase.child("users").child(myUid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        user.friendList.remove(uid);
                        mDatabase.child("users").child(myUid).setValue(user);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
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
        uid = intent.getExtras().getString("uid");
        mine = intent.getExtras().getBoolean("mine");
        add = intent.getExtras().getInt("add");
        if(add == 1) addFAB.setVisibility(View.VISIBLE);
        else if(add == 2) deleteFAB.setVisibility(View.VISIBLE);
        if(!mine) modifyMemoTextView.setVisibility(View.GONE);
        mGlideRequestManager = Glide.with(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(uid).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        latelyAchievementList.clear();
                        User user = dataSnapshot.getValue(User.class);
                        profileNameTextView.setText(user.userName);
                        mGlideRequestManager.load(user.photoUrl).into(profilePictureImageView);
                        try{
                            mGlideRequestManager.load(user.achievementList.get(0).imageUrl).into(profileLastImageView);
                        } catch (Exception ex) {
                            mGlideRequestManager.load("https://ilyricsbuzz.com/wp-content/uploads/2017/02/TWICEcoaster-LANE-2.jpg").into(profileLastImageView);
                        }
                        if(user.memo != "") {
                            memo = user.memo;
                            profileMemoTextView.setText(memo);
                        }
                        profileScoreTextView.setText(user.achievementList.size() + "");
                        profileFlagTextView.setText(user.flagList.size() + "");

                        latelyAchievementList.addAll(user.achievementList);
                        mAdapter.notifyDataSetChanged();
                        profileScrollView.scrollTo(0,0);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        latelyAchievementList = new ArrayList<>();
        mGlideRequestManager = Glide.with(this);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AchievementAdapter(this, latelyAchievementList, mGlideRequestManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        profileScrollView.scrollTo(0,0);
    }
}
