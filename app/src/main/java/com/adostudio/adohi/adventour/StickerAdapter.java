package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Sticker;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {
    private ArrayList<Sticker> stickerDataset;
    private RequestManager mRequestManager;
    private static QuestActivity activity;

    private final float bigSize = 1.2f;
    private final float smallSize = 1.0f;
    private final float bigAlpha = 1.0f;
    private final float smallAlpha = 0.8f;

    private MyApplication myApplication;

    public StickerAdapter(QuestActivity activity, ArrayList<Sticker> myStickerDataset, RequestManager requestManager) {
        this.activity = activity;
        stickerDataset = myStickerDataset;
        mRequestManager = requestManager;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public ImageView firstStickerImageView;
        public ImageView secondStickerImageView;
        public ImageView thirdStickerImageView;
        public int rowPosition;
        public ViewHolder(View v) {
            super(v);
            firstStickerImageView = (CircleImageView) v.findViewById(R.id.iv_sticker_first);
            secondStickerImageView = (CircleImageView) v.findViewById(R.id.iv_sticker_second);
            thirdStickerImageView = (CircleImageView) v.findViewById(R.id.iv_sticker_third);
            firstStickerImageView.setOnClickListener(this);
            secondStickerImageView.setOnClickListener(this);
            thirdStickerImageView.setOnClickListener(this);
            myApplication = (MyApplication) activity.getApplication();
        }

        @Override
        public void onClick(View v) {

            int id = v.getId();
            if(id == firstStickerImageView.getId()){
                firstStickerImageView.setScaleX(bigSize);
                firstStickerImageView.setScaleY(bigSize);
                firstStickerImageView.setAlpha(bigAlpha);
                secondStickerImageView.setScaleX(smallSize);
                secondStickerImageView.setScaleY(smallSize);
                secondStickerImageView.setAlpha(smallAlpha);
                thirdStickerImageView.setScaleX(smallSize);
                thirdStickerImageView.setScaleY(smallSize);
                thirdStickerImageView.setAlpha(smallAlpha);
                myApplication.setStickerPosition(rowPosition);
            }
            if(id == R.id.iv_sticker_second){
                firstStickerImageView.setScaleX(smallSize);
                firstStickerImageView.setScaleY(smallSize);
                firstStickerImageView.setAlpha(smallAlpha);
                secondStickerImageView.setScaleX(bigSize);
                secondStickerImageView.setScaleY(bigSize);
                secondStickerImageView.setAlpha(bigAlpha);
                thirdStickerImageView.setScaleX(smallSize);
                thirdStickerImageView.setScaleY(smallSize);
                thirdStickerImageView.setAlpha(smallAlpha);
                myApplication.setStickerPosition(rowPosition + 1);
            }
            if(id == R.id.iv_sticker_third){
                firstStickerImageView.setScaleX(smallSize);
                firstStickerImageView.setScaleY(smallSize);
                firstStickerImageView.setAlpha(smallAlpha);
                secondStickerImageView.setScaleX(smallSize);
                secondStickerImageView.setScaleY(smallSize);
                secondStickerImageView.setAlpha(smallAlpha);
                thirdStickerImageView.setScaleX(bigSize);
                thirdStickerImageView.setScaleY(bigSize);
                thirdStickerImageView.setAlpha(bigAlpha);
                myApplication.setStickerPosition(rowPosition + 2);
            }
            Log.d("click", v.getId() + "   " + firstStickerImageView.getId());
        }


    }

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

        try{
            mRequestManager.load(stickerDataset.get(position * 3).resId)
                    .into(holder.firstStickerImageView);
        }catch (Exception ex){

        }
        try{
            mRequestManager.load(stickerDataset.get(position * 3 + 1).resId)
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .into(holder.secondStickerImageView);
        }catch (Exception ex){

        }
        try{
            mRequestManager.load(stickerDataset.get(position * 3 + 2).resId)
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .into(holder.thirdStickerImageView);
        }catch (Exception ex){

        }
        holder.rowPosition = position;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return stickerDataset.size()/3 + 1;
    }

}
