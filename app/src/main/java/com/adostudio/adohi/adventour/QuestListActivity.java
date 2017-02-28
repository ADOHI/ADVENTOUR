package com.adostudio.adohi.adventour;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Quest;
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

public class QuestListActivity extends AppCompatActivity {

    private static final String LOGTAG = "QuestListActivity";

    private DatabaseReference appDatabase;
    private ArrayList<Quest> questArrayList;
    private RecyclerView.Adapter questAdapter;
    private RecyclerView.LayoutManager questLayoutManager;
    private RequestManager glideRequestManager;

    @BindView(R.id.rv_quest_list)RecyclerView questRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_list);
        ButterKnife.bind(this);

        questArrayList = new ArrayList<>();
        glideRequestManager = Glide.with(this);
        questRecyclerView.setHasFixedSize(true);
        questLayoutManager = new LinearLayoutManager(this);
        questRecyclerView.setLayoutManager(questLayoutManager);
        questAdapter = new QuestAdapter(this, questArrayList, glideRequestManager);
        questRecyclerView.setAdapter(questAdapter);
        questRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        appDatabase = FirebaseDatabase.getInstance().getReference();


        ActionBar ab = getSupportActionBar();
        ab.setTitle("퀘스트 목록");
    }

    @Override
    protected void onResume() {
        super.onResume();
        appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        questArrayList.clear();
                        User user = dataSnapshot.getValue(User.class);
                        questArrayList.addAll(user.getQuestList());
                        questAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
