package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.db.Achievement;
import com.adostudio.adohi.adventour.db.Review;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private static final String LOGTAG = "ReviewAdapter";

    private ArrayList<Review> reviewDataset;
    private RequestManager requestManager;
    private static AchievementDetailActivity activity;
    public ReviewAdapter(AchievementDetailActivity activity, ArrayList<Review> myReviewDataset, RequestManager myRequestManager) {
        this.activity = activity;
        reviewDataset = myReviewDataset;
        requestManager = myRequestManager;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView reviewNameTextView;
        public TextView reviewTimeTextView;
        public TextView reviewTextView;
        public ImageView reviewSumnailImageView;
        public ImageView starImageView1;
        public ImageView starImageView2;
        public ImageView starImageView3;
        public ImageView starImageView4;
        public ImageView starImageView5;



        public ViewHolder(View v) {
            super(v);
            reviewNameTextView = (TextView) v.findViewById(R.id.tv_review_name);
            reviewTimeTextView = (TextView) v.findViewById(R.id.tv_review_time);
            reviewTextView = (TextView) v.findViewById(R.id.tv_review);
            reviewSumnailImageView = (ImageView) v.findViewById(R.id.iv_review_sumnail);
            starImageView1 = (ImageView) v.findViewById(R.id.iv_reviewrow_star1);
            starImageView2 = (ImageView) v.findViewById(R.id.iv_reviewrow_star2);
            starImageView3 = (ImageView) v.findViewById(R.id.iv_reviewrow_star3);
            starImageView4 = (ImageView) v.findViewById(R.id.iv_reviewrow_star4);
            starImageView5 = (ImageView) v.findViewById(R.id.iv_reviewrow_star5);
        }

    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.reviewNameTextView.setText(reviewDataset.get(position).getName());
        holder.reviewTimeTextView.setText(reviewDataset.get(position).getTime());
        holder.reviewTextView.setText(reviewDataset.get(position).getReview());
        try{
            requestManager.load(reviewDataset.get(position).getImageUrl()).into(holder.reviewSumnailImageView);
            //holder.mainRowImageView.set
        }catch (Exception ex){
        }
        if(reviewDataset.get(position).getStar() > 1) holder.starImageView2.setImageResource(R.drawable.star_fill);
        if(reviewDataset.get(position).getStar() > 2) holder.starImageView3.setImageResource(R.drawable.star_fill);
        if(reviewDataset.get(position).getStar() > 3) holder.starImageView4.setImageResource(R.drawable.star_fill);
        if(reviewDataset.get(position).getStar() > 4) holder.starImageView5.setImageResource(R.drawable.star_fill);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reviewDataset.size();
    }
}
