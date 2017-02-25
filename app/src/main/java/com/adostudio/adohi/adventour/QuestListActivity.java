package com.adostudio.adohi.adventour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Quest;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestListActivity extends AppCompatActivity {
    private ArrayList<Quest> questArrayList;
    private RecyclerView.Adapter questAdapter;
    private RecyclerView.LayoutManager questLayoutManager;
    private RequestManager mGlideRequestManager;
    private MyApplication myApplication;
    @BindView(R.id.rv_quest_list)RecyclerView questRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_list);
        ButterKnife.bind(this);
        myApplication = (MyApplication) getApplication();

        questArrayList = new ArrayList<>();
        mGlideRequestManager = Glide.with(this);
        questRecyclerView.setHasFixedSize(true);
        questLayoutManager = new LinearLayoutManager(this);
        questRecyclerView.setLayoutManager(questLayoutManager);
        questAdapter = new QuestAdapter(this, questArrayList, mGlideRequestManager);
        questRecyclerView.setAdapter(questAdapter);
        questRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        myApplication.getMyDataBase().child("users").child(myApplication.getMyUid()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        questArrayList.clear();
                        User user = dataSnapshot.getValue(User.class);
                        questArrayList.addAll(user.questList);
                        questAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
