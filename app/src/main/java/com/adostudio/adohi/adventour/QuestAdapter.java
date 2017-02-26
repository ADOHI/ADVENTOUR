package com.adostudio.adohi.adventour;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.db.Quest;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.ViewHolder> {

    private static final String LOGTAG = "QuestAdapter";

    private ArrayList<Quest> questDataset;
    private RequestManager glideRequestManager;
    private static Activity activity;

    public QuestAdapter(Activity activity, ArrayList<Quest> myQuestDataset, RequestManager requestManager) {
        this.activity = activity;
        this.questDataset = myQuestDataset;
        this.glideRequestManager = requestManager;
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

        holder.fromTextView.setText(questDataset.get(position).getFromName());
        holder.hintTextView.setText(questDataset.get(position).getLocationHint());
        holder.locationTextView.setText(questDataset.get(position).getLocationName());
        glideRequestManager.load(questDataset.get(position).getFromImageUrl()).into(holder.fromSumnailmageView);
        glideRequestManager.load(questDataset.get(position).getReward().getResId()).into(holder.rewardlmageView);
        holder.position = position;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return questDataset.size();
    }
}
