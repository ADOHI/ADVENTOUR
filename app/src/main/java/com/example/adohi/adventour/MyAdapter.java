package com.example.adohi.adventour;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.nhn.android.maps.maplib.NGeoPoint;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<String> mTitleDataset;
    private ArrayList<String> mSumnailDataset;
    private ArrayList<String> mAddressDataset;
    private ArrayList<String> mDistanceDataset;
    private ArrayList<String> mContentIdDataset;
    private ArrayList<String> mContentTypeIdDataset;
    private ArrayList<NGeoPoint> mNGeoPointDataset;
    private RequestManager mRequestManager;
    private static MainActivity activity;
    public MyAdapter(MainActivity activity, ArrayList<String> myTitleDataset, ArrayList<String> mySumnailDataset,
                     ArrayList<String> myAddressDataset, ArrayList<String> myDistanceDataset, ArrayList<String> myContentIdDataset,
                     ArrayList<String> myContentTypeIdDataset, ArrayList<NGeoPoint> myNGeoPointDataset, RequestManager requestManager) {
        this.activity = activity;
        mTitleDataset = myTitleDataset;
        mSumnailDataset = mySumnailDataset;
        mAddressDataset = myAddressDataset;
        mDistanceDataset = myDistanceDataset;
        mContentIdDataset = myContentIdDataset;
        mContentTypeIdDataset = myContentTypeIdDataset;
        mNGeoPointDataset = myNGeoPointDataset;
        mRequestManager = requestManager;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        // each data item is just a string in this case
        private final Context context;
        public TextView achievementTitleTextView;
        public TextView achievementAddressTextView;
        public TextView achievementDistanceTextView;
        public ImageView achievementSumnailImageView;
        public ImageView bookMarkRowImageView;
        public String contentId;
        public String contentTypeId;
        public String distance;
        public NGeoPoint nGeoPoint;


        public ViewHolder(View v) {
            super(v);
            context = v.getContext();
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
            intent.putExtra("ngeopoint", nGeoPoint);
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
        holder.achievementTitleTextView.setText(mTitleDataset.get(position));
        holder.achievementSumnailImageView.setImageResource(R.drawable.btn_green_pressed);
        holder.achievementAddressTextView.setText(mAddressDataset.get(position));
        holder.achievementDistanceTextView.setText(mDistanceDataset.get(position));
        try{
            mRequestManager.load(mSumnailDataset.get(position)).into(holder.achievementSumnailImageView);
            //holder.mainRowImageView.set
        }catch (Exception ex){

        }
        holder.contentId = mContentIdDataset.get(position);
        holder.contentTypeId = mContentTypeIdDataset.get(position);
        holder.distance = mDistanceDataset.get(position);
        holder.nGeoPoint = mNGeoPointDataset.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTitleDataset.size();
    }
}
