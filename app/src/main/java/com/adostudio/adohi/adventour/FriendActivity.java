package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.nhn.android.data.DBAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    private DatabaseReference mDatabase;
    private String uid;
    private int userTrophy;
    private int userFlag;
    private int trophyRanking;
    private int flagRanking;
    private int trophySame;
    private int flagSame;
    private RequestManager mGlideRequestManager;
    private boolean issueCheck;


    private ArrayList<User> userArrayList;
    private ArrayList<User> friendArrayList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<String> friendList;
    private RecyclerView.Adapter friendAdapter;
    private RecyclerView.LayoutManager friendLayoutManager;
    @BindView(R.id.rv_friend_add)RecyclerView mRecyclerView;
    @BindView(R.id.rv_friend_list)RecyclerView friendRecyclerView;
    @BindView(R.id.ll_friend_list)LinearLayout friendLinearLayout;
    @BindView(R.id.tv_trophy_ranking)TextView trophyRankingTextView;
    @BindView(R.id.tv_flag_ranking)TextView flagRankingTextView;
    private TabLayout friendTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ButterKnife.bind(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uid).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        friendList.clear();
                        User user = dataSnapshot.getValue(User.class);
                        friendList.addAll(user.friendList);
                        userTrophy = user.achievementList.size();
                        userFlag = user.flagList.size();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mDatabase.child("users").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userArrayList.clear();
                        friendArrayList.clear();
                        trophyRanking = 1;
                        flagRanking = 1;
                        trophySame = 1;
                        flagSame = 1;
                        Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                        while(iter.hasNext()){
                            User user =  iter.next().getValue(User.class);
                            if(!user.uid.equals(uid)) {
                                if (friendList.contains(user.uid)) {
                                    friendArrayList.add(user);
                                    if(userTrophy < user.achievementList.size()) {
                                        trophyRanking += trophySame;
                                        trophySame = 1;
                                    } else if (userTrophy == user.achievementList.size()) {
                                        trophySame += 1;
                                    }

                                    if(userFlag < user.flagList.size()) {
                                        flagRanking += flagSame;
                                        flagSame = 1;
                                    } else if (userTrophy == user.achievementList.size()) {
                                        flagSame += 1;
                                    }
                                }
                                else userArrayList.add(user);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        friendAdapter.notifyDataSetChanged();
                        trophyRankingTextView.setText(trophyRanking+"등");
                        flagRankingTextView.setText(flagRanking+"등");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        friendList = new ArrayList<>();
        friendArrayList = new ArrayList<>();
        userArrayList = new ArrayList<>();
        mGlideRequestManager = Glide.with(this);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FriendAdapter(this, userArrayList, 1, mGlideRequestManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        friendRecyclerView.setHasFixedSize(true);
        friendLayoutManager = new LinearLayoutManager(this);
        friendRecyclerView.setLayoutManager(friendLayoutManager);
        friendAdapter = new FriendAdapter(this, friendArrayList, 2, mGlideRequestManager);
        friendRecyclerView.setAdapter(friendAdapter);
        friendRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        friendTabLayout = (TabLayout) findViewById(R.id.friend_tablayout);
        friendTabLayout.addOnTabSelectedListener(this);

        Intent intent = getIntent();
        issueCheck = intent.getExtras().getBoolean("issue");
        if(issueCheck){
            mRecyclerView.setVisibility(View.GONE);
            friendLinearLayout.setVisibility(View.VISIBLE);
            friendTabLayout.setVisibility(View.GONE);
            Log.d("issue", issueCheck + "");
        }

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition() == 0){
            mRecyclerView.setVisibility(View.VISIBLE);
            friendLinearLayout.setVisibility(View.GONE);
        }
        else if(tab.getPosition() == 1){
            mRecyclerView.setVisibility(View.GONE);
            friendLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public boolean getIssueCheck(){
        return issueCheck;
    }
}