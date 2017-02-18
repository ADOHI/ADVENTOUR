package com.example.adohi.adventour;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.example.adohi.adventour.db.achieve;
import com.google.android.gms.maps.model.LatLng;
import com.nhn.android.maps.maplib.NGeoPoint;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<achieve> mAchieveDataset;
    private RequestManager mRequestManager;
    private static MapsActivity activity;
    public MyAdapter(MapsActivity activity, ArrayList<achieve> myAchieveDataset , RequestManager requestManager) {
        this.activity = activity;
        mAchieveDataset = myAchieveDataset;
        mRequestManager = requestManager;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        // each data item is just a string in this case
        public TextView achievementTitleTextView;
        public TextView achievementAddressTextView;
        public TextView achievementDistanceTextView;
        public ImageView achievementSumnailImageView;
        public ImageView bookMarkRowImageView;
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
            bookMarkRowImageView = (ImageView) v.findViewById(R.id.iv_rowBookMark);
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
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
        holder.achievementTitleTextView.setText(mAchieveDataset.get(position).title);
        holder.achievementSumnailImageView.setImageResource(R.drawable.btn_green_pressed);
        holder.achievementAddressTextView.setText(mAchieveDataset.get(position).address);
        holder.achievementDistanceTextView.setText(Integer.toString((int)mAchieveDataset.get(position).distance)+"m");
        try{
            mRequestManager.load(mAchieveDataset.get(position).imageUrl).into(holder.achievementSumnailImageView);
            //holder.mainRowImageView.set
        }catch (Exception ex){

        }
        holder.contentId = mAchieveDataset.get(position).contentId;
        holder.contentTypeId = mAchieveDataset.get(position).contentTypeId;
        holder.distance = mAchieveDataset.get(position).distance;
        holder.mapX = mAchieveDataset.get(position).lng;
        holder.mapY = mAchieveDataset.get(position).lat;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mAchieveDataset.size();
    }
}
