package com.adostudio.adohi.adventour;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    private static final String LOGTAG = "StickerAdapter";

    private ArrayList<Sticker> stickerDataset;
    private RequestManager glideRequestManager;
    private static QuestActivity activity;

    private static final float BIG_SIZE = 1.2f;
    private static final float SMALL_SIZE = 1.0f;
    private static final float BIG_ALPHA = 1.0f;
    private static final float SMALL_ALPHA = 0.8f;

    public StickerAdapter(QuestActivity activity, ArrayList<Sticker> myStickerDataset, RequestManager requestManager) {
        this.activity = activity;
        stickerDataset = myStickerDataset;
        glideRequestManager = requestManager;
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
        }

        @Override
        public void onClick(View v) {

            int id = v.getId();
            if(id == firstStickerImageView.getId()){
                firstStickerImageView.setScaleX(BIG_SIZE);
                firstStickerImageView.setScaleY(BIG_SIZE);
                firstStickerImageView.setAlpha(BIG_ALPHA);
                secondStickerImageView.setScaleX(SMALL_SIZE);
                secondStickerImageView.setScaleY(SMALL_SIZE);
                secondStickerImageView.setAlpha(SMALL_ALPHA);
                thirdStickerImageView.setScaleX(SMALL_SIZE);
                thirdStickerImageView.setScaleY(SMALL_SIZE);
                thirdStickerImageView.setAlpha(SMALL_ALPHA);
                MyApplication.setStickerPosition(rowPosition);
            }
            if(id == R.id.iv_sticker_second){
                firstStickerImageView.setScaleX(SMALL_SIZE);
                firstStickerImageView.setScaleY(SMALL_SIZE);
                firstStickerImageView.setAlpha(SMALL_ALPHA);
                secondStickerImageView.setScaleX(BIG_SIZE);
                secondStickerImageView.setScaleY(BIG_SIZE);
                secondStickerImageView.setAlpha(BIG_ALPHA);
                thirdStickerImageView.setScaleX(SMALL_SIZE);
                thirdStickerImageView.setScaleY(SMALL_SIZE);
                thirdStickerImageView.setAlpha(SMALL_ALPHA);
                MyApplication.setStickerPosition(rowPosition + 1);
            }
            if(id == R.id.iv_sticker_third){
                firstStickerImageView.setScaleX(SMALL_SIZE);
                firstStickerImageView.setScaleY(SMALL_SIZE);
                firstStickerImageView.setAlpha(SMALL_ALPHA);
                secondStickerImageView.setScaleX(SMALL_SIZE);
                secondStickerImageView.setScaleY(SMALL_SIZE);
                secondStickerImageView.setAlpha(SMALL_ALPHA);
                thirdStickerImageView.setScaleX(BIG_SIZE);
                thirdStickerImageView.setScaleY(BIG_SIZE);
                thirdStickerImageView.setAlpha(BIG_ALPHA);
                MyApplication.setStickerPosition(rowPosition + 2);
            }
            Log.d("click", v.getId() + "   " + firstStickerImageView.getId());
        }


    }

    @Override
    public StickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sticker_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try{
            glideRequestManager.load(stickerDataset.get(position * 3).getResId())
                    .into(holder.firstStickerImageView);
        }catch (Exception ex){
            Log.e(LOGTAG, "sticker load failed");
        }
        try{
            glideRequestManager.load(stickerDataset.get(position * 3 + 1).getResId())
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .into(holder.secondStickerImageView);
        }catch (Exception ex){
            Log.e(LOGTAG, "sticker load failed");
        }
        try{
            glideRequestManager.load(stickerDataset.get(position * 3 + 2).getResId())
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .into(holder.thirdStickerImageView);
        }catch (Exception ex){
            Log.e(LOGTAG, "sticker load failed");
        }
        holder.rowPosition = position;
    }

    @Override
    public int getItemCount() {
        return stickerDataset.size()/3 + 1;
    }

}
