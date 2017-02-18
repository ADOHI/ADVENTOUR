package com.example.adohi.adventour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.flaviofaria.kenburnsview.KenBurnsView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GetAchivementActivity extends AppCompatActivity {
    private RequestManager mGlideRequestManager;
    @BindView(R.id.tv_get_title)TextView getTitleTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_achivement);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        KenBurnsView kbv = (KenBurnsView) findViewById(R.id.kv_get);
        mGlideRequestManager = Glide.with(this);
        try{
            mGlideRequestManager.load(intent.getExtras().getString("imageurl")).into(kbv);
        } catch (Exception ex){}
        getTitleTextView.setText(intent.getExtras().getString("title"));

    }
}
