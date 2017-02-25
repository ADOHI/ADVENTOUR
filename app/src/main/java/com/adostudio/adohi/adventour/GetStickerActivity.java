
package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Achievement;
import com.adostudio.adohi.adventour.db.Sticker;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.Glide;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class GetStickerActivity extends AppCompatActivity {
    MyApplication myApplication;
    @BindView(R.id.kv_get_sticker_sticker)KenBurnsView getStickerKenBurnView;
    @BindView(R.id.iv_get_sticker_get)CircleImageView getGetCircleImageView;
    @OnClick(R.id.iv_get_sticker_get) void getClick() {
        myApplication.getMyDataBase().child("users").child(myApplication.getMyUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        Sticker sticker = new Sticker(questAsset, questResId);
                        user.stickerList.add(sticker);
                        user.removeQuest(questLng, questLat);
                        myApplication.getMyDataBase().child("users").child(myApplication.getMyUid()).setValue(user);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
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
        myApplication = (MyApplication) getApplication();
        Intent intent = getIntent();
        questAsset = intent.getExtras().getString("questasset");
        questResId = intent.getExtras().getInt("questresid");
        questLng = intent.getExtras().getDouble("questlng");
        questLat = intent.getExtras().getDouble("questlat");
        Glide.with(this).load(questResId).into(getStickerKenBurnView);

    }
}
