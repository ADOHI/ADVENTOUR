package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
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

public class FriendActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    private static final String LOGTAG = "FriendActivity";

    private DatabaseReference appDatabase;

    private int userTrophy;
    private int userFlag;
    private int trophyRanking;
    private int flagRanking;
    private int trophySame;
    private int flagSame;
    private RequestManager glideRequestManager;
    private boolean issueCheck;


    private ArrayList<User> userArrayList;
    private ArrayList<User> friendArrayList;
    private RecyclerView.Adapter userAdapter;
    private RecyclerView.LayoutManager userLayoutManager;

    private ArrayList<String> friendList;
    private RecyclerView.Adapter friendAdapter;
    private RecyclerView.LayoutManager friendLayoutManager;
    @BindView(R.id.rv_friend_add)RecyclerView userRecyclerView;
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

        appDatabase = FirebaseDatabase.getInstance().getReference();

        friendList = new ArrayList<>();
        friendArrayList = new ArrayList<>();
        userArrayList = new ArrayList<>();
        glideRequestManager = Glide.with(this);

        userRecyclerView.setHasFixedSize(true);
        userLayoutManager = new LinearLayoutManager(this);
        userRecyclerView.setLayoutManager(userLayoutManager);
        userAdapter = new FriendAdapter(this, userArrayList, 1, glideRequestManager);
        userRecyclerView.setAdapter(userAdapter);
        userRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        friendRecyclerView.setHasFixedSize(true);
        friendLayoutManager = new LinearLayoutManager(this);
        friendRecyclerView.setLayoutManager(friendLayoutManager);
        friendAdapter = new FriendAdapter(this, friendArrayList, 2, glideRequestManager);
        friendRecyclerView.setAdapter(friendAdapter);
        friendRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        friendTabLayout = (TabLayout) findViewById(R.id.friend_tablayout);
        friendTabLayout.addOnTabSelectedListener(this);

        Intent intent = getIntent();
        issueCheck = intent.getExtras().getBoolean("issue");
        if(issueCheck){
            userRecyclerView.setVisibility(View.GONE);
            friendLinearLayout.setVisibility(View.VISIBLE);
            friendTabLayout.setVisibility(View.GONE);
        }

        ActionBar ab = getSupportActionBar();
        ab.setTitle("친구");

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition() == 0){
            userRecyclerView.setVisibility(View.VISIBLE);
            friendLinearLayout.setVisibility(View.GONE);
        }
        else if(tab.getPosition() == 1){
            userRecyclerView.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        super.onResume();
        appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        friendList.clear();
                        User user = dataSnapshot.getValue(User.class);
                        friendList.addAll(user.getFriendList());
                        userTrophy = user.getAchievementList().size();
                        userFlag = user.getFlagList().size();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        appDatabase.child("users").addListenerForSingleValueEvent(
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
                            if(!user.getUid().equals(MyApplication.getMyUid())) {
                                if (friendList.contains(user.getUid())) {
                                    friendArrayList.add(user);
                                    if(userTrophy < user.getAchievementList().size()) {
                                        trophyRanking += trophySame;
                                        trophySame = 1;
                                    } else if (userTrophy == user.getAchievementList().size()) {
                                        trophySame += 1;
                                    }

                                    if(userFlag < user.getFlagList().size()) {
                                        flagRanking += flagSame;
                                        flagSame = 1;
                                    } else if (userFlag == user.getAchievementList().size()) {
                                        flagSame += 1;
                                    }
                                }
                                else userArrayList.add(user);
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                        friendAdapter.notifyDataSetChanged();
                        trophyRankingTextView.setText(trophyRanking+"등");
                        flagRankingTextView.setText(flagRanking+"등");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
