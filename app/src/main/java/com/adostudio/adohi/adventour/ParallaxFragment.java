package com.adostudio.adohi.adventour;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.flaviofaria.kenburnsview.KenBurnsView;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

public class ParallaxFragment extends Fragment {

    private static final String LOGTAG = "ParallaxFragment";
    private static final int BACKGROUND_COLOR_LIGHT = 0xFFFFFFFF;
    private static final int BACKGROUND_COLOR_DARK = 0x7F606060;
    private static final int GLIDE_BLUR = 10;
    private static final float GLIDE_SUMNAIL = 0.1f;
    private ParallaxAdapter backgroundAdapter;
    private RequestManager glideRequestManager;
    private KenBurnsView backgroundKenBurnView;
    public static int a;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        glideRequestManager = Glide.with(this);
        View v = inflater.inflate(R.layout.demo_fragment_parallax, container, false);
        backgroundKenBurnView = (KenBurnsView) v.findViewById(R.id.kbv_main_image);
        TextView text = (TextView)v.findViewById(R.id.tv_main_name);
        TextView date = (TextView)v.findViewById(R.id.tv_main_time);
        text.setText(getArguments().getString("name"));
        date.setText(getArguments().getString("date"));
        try {
            Log.d(LOGTAG, getArguments().getString("imageurl"));
            if (!getArguments().getBoolean("blur")) {

                glideRequestManager.load(getArguments().getString("imageurl"))
                        .thumbnail(GLIDE_SUMNAIL)
                        .into(backgroundKenBurnView);
                text.setTextColor(BACKGROUND_COLOR_LIGHT);
                date.setTextColor(BACKGROUND_COLOR_LIGHT);
            }

            else {
                    glideRequestManager.load(getArguments().getString("imageurl"))
                            .bitmapTransform(new BlurTransformation(getContext(), GLIDE_BLUR), new GrayscaleTransformation(getContext()))
                            .thumbnail(GLIDE_SUMNAIL)
                            .into(backgroundKenBurnView);
                    text.setTextColor(BACKGROUND_COLOR_DARK);
                    date.setTextColor(BACKGROUND_COLOR_DARK);
                }
        } catch (Exception ex){
            Log.e(LOGTAG, "image loading error");
            if (!getArguments().getBoolean("blur")) {

                glideRequestManager.load(R.drawable.no_image)
                        .thumbnail(GLIDE_SUMNAIL)
                        .into(backgroundKenBurnView);
                text.setTextColor(BACKGROUND_COLOR_LIGHT);
                date.setTextColor(BACKGROUND_COLOR_LIGHT);
            }

            else {
                glideRequestManager.load(R.drawable.no_image)
                        .bitmapTransform(new BlurTransformation(getContext(), GLIDE_BLUR), new GrayscaleTransformation(getContext()))
                        .thumbnail(GLIDE_SUMNAIL)
                        .into(backgroundKenBurnView);
                text.setTextColor(BACKGROUND_COLOR_DARK);
                date.setTextColor(BACKGROUND_COLOR_DARK);
            }
        }


        return v;
    }

    public void setAdapter(ParallaxAdapter catsAdapter) {
        backgroundAdapter = catsAdapter;
    }
}
