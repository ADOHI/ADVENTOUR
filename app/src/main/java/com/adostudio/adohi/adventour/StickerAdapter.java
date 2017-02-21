package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.db.Sticker;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {
    private ArrayList<Sticker> stickerDataset;
    private RequestManager mRequestManager;
    private static QuestActivity activity;
    public StickerAdapter(QuestActivity activity, ArrayList<Sticker> myStickerDataset, RequestManager requestManager) {
        this.activity = activity;
        stickerDataset = myStickerDataset;
        mRequestManager = requestManager;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public ImageView firstStickerImageView;
        public ImageView secondStickerImageView;
        public ImageView thirdStickerImageView;

        public ViewHolder(View v) {
            super(v);
            firstStickerImageView = (ImageView) v.findViewById(R.id.iv_sticker_first);
            secondStickerImageView = (ImageView) v.findViewById(R.id.iv_sticker_second);
            thirdStickerImageView = (ImageView) v.findViewById(R.id.iv_sticker_third);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent = new Intent(activity, AchievementDetailActivity.class);

            if(id == R.id.iv_sticker_first){

            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)


    // Create new views (invoked by the layout manager)
    @Override
    public StickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sticker_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

       Log.d("aaaa", "Sticker" + stickerDataset.size());

        try{
            mRequestManager.load(stickerDataset.get(position * 3).resId).into(holder.firstStickerImageView);
        }catch (Exception ex){

        }
        try{
            mRequestManager.load(stickerDataset.get(position * 3 + 1).resId).into(holder.secondStickerImageView);
        }catch (Exception ex){

        }
        try{
            mRequestManager.load(stickerDataset.get(position * 3 + 2).resId).into(holder.thirdStickerImageView);
        }catch (Exception ex){

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return stickerDataset.size()/3 + 1;
    }
}
