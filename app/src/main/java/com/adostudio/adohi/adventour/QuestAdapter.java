package com.adostudio.adohi.adventour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Quest;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.ViewHolder> {
    private ArrayList<Quest> questDataset;
    private RequestManager mRequestManager;
    private static Activity activity;

    public QuestAdapter(Activity activity, ArrayList<Quest> myQuestDataset, RequestManager requestManager) {
        this.activity = activity;
        this.questDataset = myQuestDataset;
        this.mRequestManager = requestManager;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public TextView fromTextView;
        public TextView hintTextView;
        public TextView locationTextView;
        public ImageView fromSumnailmageView;
        public ImageView rewardlmageView;
        public int position;
        public ViewHolder(View v) {
            super(v);
            fromTextView = (TextView) v.findViewById(R.id.tv_quest_list_from);
            hintTextView = (TextView) v.findViewById(R.id.tv_quest_list_hint);
            locationTextView = (TextView) v.findViewById(R.id.tv_quest_list_location);
            fromSumnailmageView = (ImageView) v.findViewById(R.id.iv_quest_list_from);
            rewardlmageView = (ImageView) v.findViewById(R.id.iv_quest_list_reward);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent = new Intent(activity, QuestIssueActivity.class);
            intent.putExtra("issue", false);
            intent.putExtra("position", position);
            activity.startActivity(intent);
        }
    }

    @Override
    public QuestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quest_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.fromTextView.setText(questDataset.get(position).fromName);
        holder.hintTextView.setText(questDataset.get(position).locationHint);
        holder.locationTextView.setText(questDataset.get(position).locationName);
        mRequestManager.load(questDataset.get(position).fromImageUrl).into(holder.fromSumnailmageView);
        mRequestManager.load(questDataset.get(position).reward.resId).into(holder.rewardlmageView);
        holder.position = position;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return questDataset.size();
    }
}
