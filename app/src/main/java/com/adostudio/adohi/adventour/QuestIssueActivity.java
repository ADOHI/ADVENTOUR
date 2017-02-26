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

    private static final String LOGTAG = "QuestIssueActivity";

    private DatabaseReference appDatabase;
    private RequestManager glideRequestManager;
    private String uid;
    private String userName;
    private String photoUrl;
    private String questAsset;
    private boolean issueCheck;
    private int questPosition;
    private double questLng;
    private double questLat;
    private int questResId;
    private Sticker sticker;
    public static String friendUid;
    public static String friendName;
    public static String friendUrl;
    public static String locationName;
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
            appDatabase.child("users").child(friendUid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            Quest quest = new Quest(sticker, uid, userName, photoUrl, friendUid,
                                    friendName, friendUrl, locationName,
                                    hintEditText.getText().toString(), MyApplication.getCurrentLng(), MyApplication.getCurrentLat());
                            user.addQuestList(quest);
                            appDatabase.child("users").child(friendUid).setValue(user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(LOGTAG, "database error = " + databaseError);
                        }
                    });
            appDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            user.removeStickerList(questPosition);
                            appDatabase.child("users").child(uid).setValue(user);
                            MyApplication.setStickerPosition(-1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(LOGTAG, "database error = " + databaseError);
                        }
                    });
            finish();
            MyApplication.init();
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
        glideRequestManager = Glide.with(this);
        appDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            uid = user.getUid();
            userName = user.getDisplayName();
            photoUrl = user.getPhotoUrl().toString();
            glideRequestManager.load(user.getPhotoUrl()).into(fromSumnailImageView);
            fromNameTextView.setText(user.getDisplayName());
        }


        Intent intent = getIntent();
        issueCheck = intent.getExtras().getBoolean("issue");
        questPosition = intent.getExtras().getInt("position");
        if(!issueCheck) {
            appDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            Quest quest = user.getQuestList().get(questPosition);
                            glideRequestManager.load(quest.getFromImageUrl()).into(fromSumnailImageView);
                            fromNameTextView.setText(quest.getFromName());
                            glideRequestManager.load(quest.getToImageUrl()).into(toSumnailImageView);
                            toNameTextView.setText(quest.getToName());
                            locationTextView.setText(quest.getLocationName());
                            glideRequestManager.load(quest.getReward().getResId()).into(questStickerImageView);
                            hintEditText.setText(quest.getLocationHint());
                            hintEditText.setFocusable(false);
                            hintEditText.setClickable(false);
                            questLng = quest.getLocationLng();
                            questLat = quest.getLocationLat();
                            questAsset = quest.getReward().getAssetName();
                            questResId = quest.getReward().getResId();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(LOGTAG, "database error = " + databaseError);

                        }
                    });
        } else {

            appDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            sticker = user.getStickerList().get(questPosition);
                            glideRequestManager.load(sticker.getResId()).into(questStickerImageView);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(LOGTAG, "database error = " + databaseError);

                        }
                    });

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MyApplication.getIssueFriendImageUrl() != null && MyApplication.getIssueFriendName() != null) {
            Glide.with(this).load(MyApplication.getIssueFriendImageUrl()).into(toSumnailImageView);
            toNameTextView.setText(MyApplication.getIssueFriendName());
        }
        if(MyApplication.getIssueLocationName() != null) {
            locationTextView.setText(MyApplication.getIssueLocationName());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.init();
    }
}
