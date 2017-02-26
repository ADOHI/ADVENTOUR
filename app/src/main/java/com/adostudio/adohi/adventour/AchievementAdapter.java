package com.adostudio.adohi.adventour;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.db.Achievement;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private static final String LOGTAG = "AcievementAdapter";

    private ArrayList<Achievement> achievementDataset;
    private RequestManager achievementRequestManager;
    private static Activity activity;
    public AchievementAdapter(Activity activity, ArrayList<Achievement> myAchievementDataset, RequestManager requestManager) {
        this.activity = activity;
        achievementDataset = myAchievementDataset;
        achievementRequestManager = requestManager;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public TextView achievementTitleTextView;
        public TextView achievementAddressTextView;
        public TextView achievementDateTextView;
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
            achievementDateTextView = (TextView) v.findViewById(R.id.tv_achievement_distance);
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
        View achievementRowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.achievement_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder achievementViewHolder = new ViewHolder(achievementRowView);
        return achievementViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get_button element from your dataset at this position
        // - replace the contents of the view with that element
        holder.achievementTitleTextView.setText(achievementDataset.get(position).getTitle());
        holder.achievementAddressTextView.setText(achievementDataset.get(position).getAddress());
        holder.achievementDateTextView.setText(achievementDataset.get(position).getTime());

        try{
            achievementRequestManager.load(achievementDataset.get(position).getImageUrl()).into(holder.achievementSumnailImageView);
            //holder.mainRowImageView.set
        }catch (Exception ex){

        }
        holder.contentId = achievementDataset.get(position).getContentId();
        holder.contentTypeId = achievementDataset.get(position).getContentTypeId();
        holder.distance = achievementDataset.get(position).getDistance();
        holder.mapX = achievementDataset.get(position).getLng();
        holder.mapY = achievementDataset.get(position).getLat();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return achievementDataset.size();
    }
}
