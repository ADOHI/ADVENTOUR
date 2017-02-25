package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Quest;
import com.adostudio.adohi.adventour.db.Sticker;
import com.adostudio.adohi.adventour.db.User;
import com.adostudio.adohi.adventour.userdefinedtargets.UserDefinedTargets;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuestIssueActivity extends AppCompatActivity {
    private MyApplication myapp;
    private DatabaseReference mDatabase;
    private RequestManager mGlideRequestManager;
    private String uid;
    private String userName;
    private String photoUrl;
    private String questAsset;
    private int stickerIndex;
    private boolean issueCheck;
    private int questPosition;
    private double questLng;
    private double questLat;
    private int questResId;
    private Sticker sticker;
    @BindView(R.id.iv_quest_sticker)ImageView questStickerImageView;
    @BindView(R.id.et_issue_hint)EditText hintEditText;
    @BindView(R.id.iv_issue_from_sumnail)ImageView fromSumnailImageView;
    @BindView(R.id.tv_issue_from_name)TextView fromNameTextView;
    @BindView(R.id.iv_issue_to_sumnail)ImageView toSumnailImageView;
    @BindView(R.id.tv_issue_to_name)TextView toNameTextView;
    @OnClick(R.id.iv_issue_to_sumnail) void toSumnailClick(){
        Intent intent = new Intent(this, FriendActivity.class);
        intent.putExtra("issue", true);
        startActivity(intent);
    }
    @BindView(R.id.tv_issue_location)TextView locationTextView;
    @BindView(R.id.iv_issue_location)ImageView locationImageView;
    @OnClick(R.id.iv_issue_location) void locationClick() {
        Intent intent = new Intent(this, IssueLocationActivity.class);
        startActivity(intent);
    }
    @BindView(R.id.iv_issue_stamp)ImageView stampImageView;
    @OnClick(R.id.iv_issue_stamp) void stampClick(){
        if (issueCheck) {
            mDatabase.child("users").child(myapp.getIssueFriendUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            Quest quest = new Quest(sticker, uid, userName, photoUrl, myapp.getIssueFriendUid(),
                                    myapp.getIssueFriendName(), myapp.getIssueFriendImageUrl(), myapp.getIssueLocationName(),
                                    hintEditText.getText().toString(), myapp.getCurrentLng(), myapp.getCurrentLat());
                            user.questList.add(0, quest);
                            mDatabase.child("users").child(myapp.getIssueFriendUid()).setValue(user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            mDatabase.child("users").child(myapp.getMyUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            user.stickerList.remove(questPosition);
                            myapp.setStickerPosition(-1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            finish();
        } else {
            Intent intent = new Intent(this, UserDefinedTargets.class);
            intent.putExtra("questlng", questLng);
            intent.putExtra("questlat", questLat);
            intent.putExtra("questasset", questAsset);
            intent.putExtra("questresid", questResId);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_issue);
        ButterKnife.bind(this);
        myapp = (MyApplication)getApplication();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mGlideRequestManager = Glide.with(this);
        Intent intent = getIntent();
        issueCheck = intent.getExtras().getBoolean("issue");
        questPosition = intent.getExtras().getInt("position");
        if(!issueCheck) {
            mDatabase.child("users").child(myapp.getMyUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            Quest quest = user.questList.get(questPosition);
                            mGlideRequestManager.load(quest.fromImageUrl).into(fromSumnailImageView);
                            fromNameTextView.setText(quest.fromName);
                            mGlideRequestManager.load(quest.toImageUrl).into(toSumnailImageView);
                            toNameTextView.setText(quest.toName);
                            locationTextView.setText(quest.locationName);
                            mGlideRequestManager.load(quest.reward.resId).into(questStickerImageView);
                            hintEditText.setText(quest.locationHint);
                            hintEditText.setFocusable(false);
                            hintEditText.setClickable(false);
                            questLng = quest.locationLng;
                            questLat = quest.locationLat;
                            questAsset = quest.reward.assetName;
                            questResId = quest.reward.resId;
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                uid = user.getUid();
                userName = user.getDisplayName();
                photoUrl = user.getPhotoUrl().toString();
                Glide.with(this).load(user.getPhotoUrl()).into(fromSumnailImageView);
                fromNameTextView.setText(user.getDisplayName());
            }
            mDatabase.child("users").child(myapp.getMyUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            sticker = user.stickerList.get(questPosition);
                            mGlideRequestManager.load(sticker.resId).into(questStickerImageView);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(myapp.getIssueFriendImageUrl() != null && myapp.getIssueFriendName() != null) {
            Glide.with(this).load(myapp.getIssueFriendImageUrl()).into(toSumnailImageView);
            toNameTextView.setText(myapp.getIssueFriendName());
        }
        if(myapp.getIssueLocationName() != null) {
            locationTextView.setText(myapp.getIssueLocationName());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myapp.setIssueFriendUid(null);
        myapp.setIssueFriendName(null);
        myapp.setIssueFriendImageUrl(null);
        myapp.setIssueLocationName(null);
    }
}
