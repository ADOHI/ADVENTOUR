package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.db.Conquest;
import com.adostudio.adohi.adventour.db.Sticker;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.flaviofaria.kenburnsview.KenBurnsView;
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
import tyrantgit.explosionfield.ExplosionField;

public class GetAchivementActivity extends AppCompatActivity {
    private RequestManager mGlideRequestManager;
    private DatabaseReference mDatabase;
    private String uid;
    private String exUid = "";
    private String contentId;
    private String name;
    private String imageUrl;
    private ExplosionField explosionField;
    private boolean getBoolean = false;
    private int index;
    private int resid;
    @BindView(R.id.iv_get_flag)ImageView getFlagImageView;
    @BindView(R.id.tv_get_flag)TextView getFlagTextView;
    @BindView(R.id.iv_get_chest)ImageView getChestImageView;
    @OnClick(R.id.iv_get_chest)void chestClick(){
        index = (int) (Math.random() * 9);
        resid = index + R.drawable.tw1;
        mGlideRequestManager.load(resid).into(getChestImageView);
        getBoolean = true;
        mGlideRequestManager.load(R.drawable.get).into(getKickImageView);
    }
    @BindView(R.id.tv_get_title)TextView getTitleTextView;
    @BindView(R.id.iv_get_photo)ImageView getPhotoImageView;
    @BindView((R.id.tv_get_name))TextView getNameTextView;
    @BindView(R.id.iv_get_kick)ImageView getKickImageView;
    @OnClick(R.id.iv_get_kick)void kickClick(){
        if(!getBoolean) {
            mDatabase.child("conquests").child(contentId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Conquest conquest = new Conquest(contentId, uid, name, imageUrl);
                            mDatabase.child("conquests").child(contentId).setValue(conquest);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            user.flagList.add(user.achievementList.get(0));
                            mDatabase.child("users").child(uid).setValue(user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
            if(exUid != ""){
                mDatabase.child("users").child(exUid).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                user.removeFlag(contentId);
                                mDatabase.child("users").child(exUid).setValue(user);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }

            explosionField.explode(getPhotoImageView);
            explosionField.explode(getNameTextView);
            getFlagImageView.setVisibility(View.VISIBLE);
            getFlagTextView.setVisibility(View.VISIBLE);
            getChestImageView.setVisibility(View.VISIBLE);
        } else {
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            Sticker sticker = new Sticker("tw"+(index+1)+".jpg", resid);
                            user.stickerList.add(sticker);
                            mDatabase.child("users").child(uid).setValue(user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
            finish();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_achivement);
        ButterKnife.bind(this);
        explosionField = new ExplosionField(this);
        Intent intent = getIntent();
        contentId = intent.getExtras().getString("contentid");
        KenBurnsView kbv = (KenBurnsView) findViewById(R.id.kv_get);
        mGlideRequestManager = Glide.with(this);
        try{
            mGlideRequestManager.load(intent.getExtras().getString("imageurl")).into(kbv);
        } catch (Exception ex){}
        getTitleTextView.setText(intent.getExtras().getString("title"));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            imageUrl = user.getPhotoUrl().toString();
            uid = user.getUid();
        } else {
            // No user is signed in
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("conquests").child(contentId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       if(dataSnapshot.exists()){
                           Conquest conquest = dataSnapshot.getValue(Conquest.class);
                           mGlideRequestManager.load(conquest.imageUrl).into(getPhotoImageView);
                           getNameTextView.setText(conquest.name);
                           exUid = uid;
                           if(conquest.uid == uid){
                               getKickImageView.setVisibility(View.INVISIBLE);
                           }
                       }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }
}
