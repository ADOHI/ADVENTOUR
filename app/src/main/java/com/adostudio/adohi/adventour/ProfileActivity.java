package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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

    private static final String LOGTAG = "ProfileActivity";

    private String friendUid;
    private String myUid;
    private String memo = null;
    private int add;
    private boolean mine;
    private RequestManager glideRequestManager;
    private DatabaseReference appDatabase;
    private RecyclerView.Adapter latelyAchievementAdapter;
    private RecyclerView.LayoutManager latelyAchievementLayoutManager;
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
            intent.putExtra("uid", myUid);
            startActivity(intent);
        }
    }
    @BindView(R.id.fab_profile_add)FloatingActionButton addFAB;
    @OnClick(R.id.fab_profile_add)void addFABClick(){

        appDatabase.child("users").child(myUid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);
                        user.addFriendList(friendUid);
                        appDatabase.child("users").child(myUid).child("friendList").setValue(user.getFriendList());
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

        appDatabase.child("users").child(myUid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        user.removeFriendList(friendUid);
                        appDatabase.child("users").child(myUid).child("friendList").setValue(user.getFriendList());
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        } else {
            Log.d(LOGTAG, "user unsigned");
        }

        appDatabase = FirebaseDatabase.getInstance().getReference();

        String uid;

        if(mine)uid = myUid;
        else uid = friendUid;
        appDatabase.child("users").child(uid).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        latelyAchievementList.clear();
                        User user = dataSnapshot.getValue(User.class);
                        profileNameTextView.setText(user.getUserName());
                        glideRequestManager.load(user.getPhotoUrl()).into(profilePictureImageView);
                        try{
                            glideRequestManager.load(user.getAchievementList().get(0).getImageUrl()).into(profileLastImageView);
                        } catch (Exception ex) {
                            glideRequestManager.load(R.string.default_image).into(profileLastImageView);
                        }
                        if(!user.getMemo().equals("")) {
                            memo = user.getMemo();
                            profileMemoTextView.setText(memo);
                        } else profileMemoTextView.setText(getString(R.string.intro_default));
                        profileScoreTextView.setText(user.getAchievementList().size() + "");
                        profileFlagTextView.setText(user.getFlagList().size() + "");

                        latelyAchievementList.addAll(user.getAchievementList());
                        latelyAchievementAdapter.notifyDataSetChanged();
                        profileScrollView.scrollTo(0,0);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error : " + databaseError);

                    }
                });

        latelyAchievementList = new ArrayList<>();
        glideRequestManager = Glide.with(this);
        mRecyclerView.setHasFixedSize(true);
        latelyAchievementLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(latelyAchievementLayoutManager);
        latelyAchievementAdapter = new AchievementAdapter(this, latelyAchievementList, glideRequestManager);
        mRecyclerView.setAdapter(latelyAchievementAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        profileScrollView.scrollTo(0,0);
    }
}
