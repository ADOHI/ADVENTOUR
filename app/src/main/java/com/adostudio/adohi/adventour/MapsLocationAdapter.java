package com.adostudio.adohi.adventour;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.bumptech.glide.RequestManager;
import com.adostudio.adohi.adventour.db.Achievement;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class MapsLocationAdapter extends RecyclerView.Adapter<MapsLocationAdapter.ViewHolder> {

    private static final String LOGTAG = "MapsLocationAdapter";

    private ArrayList<Achievement> achievementDataset;
    private RequestManager glideRequestManager;
    private static Activity activity;
    public MapsLocationAdapter(Activity activity, ArrayList<Achievement> myAchievementDataset, RequestManager requestManager) {
        this.activity = activity;
        achievementDataset = myAchievementDataset;
        glideRequestManager = requestManager;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public TextView achievementTitleTextView;
        public TextView achievementAddressTextView;
        public TextView achievementDistanceTextView;
        public ImageView achievementSumnailImageView;
        public String contentId;
        public String contentTypeId;
        public String achievementName;
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
            if(activity.getClass() == IssueLocationActivity.class){
                MyApplication.setIssueLocationName(achievementName);
                activity.finish();
            } else {
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
    }


    @Override
    public MapsLocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View achievementRowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.achievement_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(achievementRowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.achievementTitleTextView.setText(achievementDataset.get(position).getTitle());
        holder.achievementSumnailImageView.setImageResource(R.drawable.btn_green_pressed);
        holder.achievementAddressTextView.setText(achievementDataset.get(position).getAddress());

        if( achievementDataset.get(position).getDistance() > 1000) {
            String num = String.format("%.2f" , (achievementDataset.get(position).getDistance())/1000);
            holder.achievementDistanceTextView.setText(num+"km");
        } else {
            holder.achievementDistanceTextView.setText(Integer.toString((int) achievementDataset.get(position).getDistance()) + "m");
        }
        try{
            glideRequestManager.load(achievementDataset.get(position).getImageUrl())
                    .thumbnail(0.1f)
                    .into(holder.achievementSumnailImageView);
        }catch (Exception ex){
            glideRequestManager.load(R.drawable.no_image)
                    .thumbnail(0.1f)
                    .into(holder.achievementSumnailImageView);
        }
        holder.contentId = achievementDataset.get(position).getContentId();
        holder.contentTypeId = achievementDataset.get(position).getContentTypeId();
        holder.distance = achievementDataset.get(position).getDistance();
        holder.mapX = achievementDataset.get(position).getLng();
        holder.mapY = achievementDataset.get(position).getLat();
        holder.achievementName = achievementDataset.get(position).getTitle();
    }

    @Override
    public int getItemCount() {
        return achievementDataset.size();
    }
}
