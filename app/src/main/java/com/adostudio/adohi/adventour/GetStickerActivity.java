
package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.adostudio.adohi.adventour.db.Sticker;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.Glide;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class GetStickerActivity extends AppCompatActivity {

    private static final String LOGTAG = "GetStickerActivity";

    private DatabaseReference appDatabase;
    private String uid;
    @BindView(R.id.kv_get_sticker_sticker)KenBurnsView getStickerKenBurnView;
    @BindView(R.id.iv_get_sticker_get)CircleImageView getGetCircleImageView;
    @OnClick(R.id.iv_get_sticker_get) void getClick() {
        appDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        Sticker sticker = new Sticker(questAsset, questResId);
                        user.addStickerList(sticker);
                        user.removeQuest(questLng, questLat);
                        appDatabase.child("users").child(uid).setValue(user);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        finish();
    }
    private String questAsset;
    private int questResId;
    private double questLng;
    private double questLat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_sticker);
        ButterKnife.bind(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }
        appDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        questAsset = intent.getExtras().getString("questasset");
        questResId = intent.getExtras().getInt("questresid");
        questLng = intent.getExtras().getDouble("questlng");
        questLat = intent.getExtras().getDouble("questlat");
        Glide.with(this).load(questResId).into(getStickerKenBurnView);

    }
}
