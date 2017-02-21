package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.adostudio.adohi.adventour.db.Sticker;
import com.adostudio.adohi.adventour.db.User;
import com.adostudio.adohi.adventour.userdefinedtargets.UserDefinedTargets;
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

public class QuestActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String uid;
    private RequestManager mGlideRequestManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Sticker> stickerArrayList;

    private int testid;

    @BindView(R.id.rv_sticker_list)RecyclerView mRecyclerView;
    @BindView(R.id.iv_quest_camera)ImageView questCameraImageView;
    @OnClick(R.id.iv_quest_camera)void questCameraClick() {

        Intent intent = new Intent(this, UserDefinedTargets.class);
        intent.putExtra("test", "tw1.jpg");
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        uid = intent.getExtras().getString("uid");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        stickerArrayList.addAll(user.stickerList);
                        mAdapter.notifyDataSetChanged();
                        Log.d("aaaa", stickerArrayList.size() + " " + user.stickerList.size() + " ");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        stickerArrayList = new ArrayList<>();
        mGlideRequestManager = Glide.with(this);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new StickerAdapter(this, stickerArrayList, mGlideRequestManager);
        mRecyclerView.setAdapter(mAdapter);

    }
}
