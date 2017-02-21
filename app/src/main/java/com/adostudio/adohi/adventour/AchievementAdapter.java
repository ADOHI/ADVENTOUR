package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.db.Achievement;
import com.bumptech.glide.RequestManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {
    private ArrayList<Achievement> mAchievementDataset;
    private RequestManager mRequestManager;
    private static ProfileActivity activity;
    public AchievementAdapter(ProfileActivity activity, ArrayList<Achievement> myAchievementDataset, RequestManager requestManager) {
        this.activity = activity;
        mAchievementDataset = myAchievementDataset;
        mRequestManager = requestManager;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public TextView achievementTitleTextView;
        public TextView achievementAddressTextView;
        public TextView achievementDistanceTextView;
        public ImageView achievementSumnailImageView;
        public String contentId;
        public String contentTypeId;
        public double distance;
        public double mapX;
        public double mapY;


        public ViewHolder(View v) {
            super(v);
            achievementTitleTextView = (TextView) v.findViewById(R.id.tv_achievement_title);
            achievementAddressTextView = (TextView) v.findViewById(R.id.tv_achievement_address);
            achievementDistanceTextView = (TextView) v.findViewById(R.id.tv_achievement_distance);
            achievementSumnailImageView = (ImageView) v.findViewById(R.id.iv_achievement_sumnail);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent = new Intent(activity, AchievementDetailActivity.class);
            intent.putExtra("contentid", contentId);
            intent.putExtra("contenttypeid", contentTypeId);
            intent.putExtra("distance", distance);
            intent.putExtra("mapx", mapX);
            intent.putExtra("mapy", mapY);
            activity.startActivity(intent);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)


    // Create new views (invoked by the layout manager)
    @Override
    public AchievementAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.achievement_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.achievementTitleTextView.setText(mAchievementDataset.get(position).title);
        holder.achievementSumnailImageView.setImageResource(R.drawable.btn_green_pressed);
        holder.achievementAddressTextView.setText(mAchievementDataset.get(position).address);
        holder.achievementDistanceTextView.setText(mAchievementDataset.get(position).time);

        try{
            mRequestManager.load(mAchievementDataset.get(position).imageUrl).into(holder.achievementSumnailImageView);
            //holder.mainRowImageView.set
        }catch (Exception ex){

        }
        holder.contentId = mAchievementDataset.get(position).contentId;
        holder.contentTypeId = mAchievementDataset.get(position).contentTypeId;
        holder.distance = mAchievementDataset.get(position).distance;
        holder.mapX = mAchievementDataset.get(position).lng;
        holder.mapY = mAchievementDataset.get(position).lat;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mAchievementDataset.size();
    }
}
