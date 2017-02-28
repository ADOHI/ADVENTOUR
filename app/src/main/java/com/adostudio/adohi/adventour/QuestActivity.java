package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Sticker;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuestActivity extends AppCompatActivity {

    private static final String LOGTAG = "QuestActivity";

    private DatabaseReference appDatabase;
    private RequestManager glideRequestManager;
    private RecyclerView.Adapter stickerAdapter;
    private RecyclerView.LayoutManager stickerLayoutManager;
    private ArrayList<Sticker> stickerArrayList;

    @BindView(R.id.rv_sticker_list)RecyclerView stickerRecyclerView;
    @BindView(R.id.iv_quest_camera)ImageView questCameraImageView;
    @OnClick(R.id.iv_quest_camera)void questCameraClick() {
        Intent intent = new Intent(this, QuestIssueActivity.class);
        intent.putExtra("issue", true);
        intent.putExtra("position", MyApplication.getStickerPosition());
        startActivity(intent);
    }
    @BindView(R.id.iv_quest_quest)ImageView questImageView;
    @OnClick(R.id.iv_quest_quest)void questClick() {

        Intent intent = new Intent(this, QuestListActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);
        ButterKnife.bind(this);

        questCameraImageView.setOnTouchListener(MyApplication.getTouchImage());
        questImageView.setOnTouchListener(MyApplication.getTouchImage());

        appDatabase = FirebaseDatabase.getInstance().getReference();

        stickerArrayList = new ArrayList<>();
        glideRequestManager = Glide.with(this);
        stickerRecyclerView.setHasFixedSize(true);
        stickerLayoutManager = new LinearLayoutManager(this);
        stickerRecyclerView.setLayoutManager(stickerLayoutManager);
        stickerAdapter = new StickerAdapter(this, stickerArrayList, glideRequestManager);
        stickerRecyclerView.setAdapter(stickerAdapter);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("퀘스트");
    }

    @Override
    protected void onResume() {
        appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        stickerArrayList.clear();
                        User user = dataSnapshot.getValue(User.class);
                        stickerArrayList.addAll(user.getStickerList());
                        stickerAdapter.notifyDataSetChanged();
                        Log.d(LOGTAG, stickerArrayList.size() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.init();
    }
}
