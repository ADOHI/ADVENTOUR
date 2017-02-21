package com.adostudio.adohi.adventour;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flaviofaria.kenburnsview.KenBurnsView;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

public class DemoParallaxFragment extends Fragment {

    private DemoParallaxAdapter mCatsAdapter;
    private RequestManager mGlideRequestManager;
    private KenBurnsView image;
    public static int a;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        mGlideRequestManager = Glide.with(this);
        View v = inflater.inflate(R.layout.demo_fragment_parallax, container, false);
        image = (KenBurnsView) v.findViewById(R.id.image);
        TextView text = (TextView)v.findViewById(R.id.name);
        TextView date = (TextView)v.findViewById(R.id.time);
        text.setText(getArguments().getString("name"));
        date.setText(getArguments().getString("date"));
        try {
            if (!getArguments().getBoolean("blur")) {
                mGlideRequestManager.load(getArguments().getString("imageurl"))
                        .diskCacheStrategy( DiskCacheStrategy.NONE )
                        .thumbnail(0.1f)
                        .into(image);
                text.setTextColor(0xFFFFFFFF);
            }

            else {
                    mGlideRequestManager.load(getArguments().getString("imageurl"))
                            .bitmapTransform(new BlurTransformation(getContext(), 10), new GrayscaleTransformation(getContext()))
                            .diskCacheStrategy( DiskCacheStrategy.NONE )
                            .thumbnail(0.1f)
                            .into(image);
                    text.setTextColor(0x7F606060);
                }
        } catch (Exception ex){
            Log.d("Exception", "glide");
        }


        return v;
    }

    public void setAdapter(DemoParallaxAdapter catsAdapter) {
        mCatsAdapter = catsAdapter;
    }
    public static void imageBlur(boolean a){

    }
}
