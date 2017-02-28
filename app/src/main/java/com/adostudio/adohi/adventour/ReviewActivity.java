package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.Review;
import com.adostudio.adohi.adventour.db.ReviewAchievement;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReviewActivity extends AppCompatActivity {

    private static final String LOGTAG = "ReviewActivity";

    private static final int DEFAULT_SCORE = 3;
    private int score = DEFAULT_SCORE;
    private DatabaseReference appDatabase;
    private String contentId;
    @BindView(R.id.iv_review_star1)ImageView starIV1;
    @BindView(R.id.iv_review_star2)ImageView starIV2;
    @BindView(R.id.iv_review_star3)ImageView starIV3;
    @BindView(R.id.iv_review_star4)ImageView starIV4;
    @BindView(R.id.iv_review_star5)ImageView starIV5;
    @BindView(R.id.tv_review_score)TextView starScoreTV;
    @BindView(R.id.et_review)EditText reviewEditText;
    @OnClick(R.id.iv_review_star1)void star1Click(){
        score = 1;
        starScoreTV.setText("별점 " + score);
        starIV1.setImageResource(R.drawable.star_fill);
        starIV2.setImageResource(R.drawable.star);
        starIV3.setImageResource(R.drawable.star);
        starIV4.setImageResource(R.drawable.star);
        starIV5.setImageResource(R.drawable.star);
    }

    @OnClick(R.id.iv_review_star2)void star2Click(){
        score = 2;
        starScoreTV.setText("별점 " + score);
        starIV1.setImageResource(R.drawable.star_fill);
        starIV2.setImageResource(R.drawable.star_fill);
        starIV3.setImageResource(R.drawable.star);
        starIV4.setImageResource(R.drawable.star);
        starIV5.setImageResource(R.drawable.star);
    }

    @OnClick(R.id.iv_review_star3)void star3Click(){
        score = 3;
        starScoreTV.setText("별점 " + score);
        starIV1.setImageResource(R.drawable.star_fill);
        starIV2.setImageResource(R.drawable.star_fill);
        starIV3.setImageResource(R.drawable.star_fill);
        starIV4.setImageResource(R.drawable.star);
        starIV5.setImageResource(R.drawable.star);
    }

    @OnClick(R.id.iv_review_star4)void star4Click(){
        score = 4;
        starScoreTV.setText("별점 " + score);
        starIV1.setImageResource(R.drawable.star_fill);
        starIV2.setImageResource(R.drawable.star_fill);
        starIV3.setImageResource(R.drawable.star_fill);
        starIV4.setImageResource(R.drawable.star_fill);
        starIV5.setImageResource(R.drawable.star);
    }

    @OnClick(R.id.iv_review_star5)void star5Click(){
        score = 5;
        starScoreTV.setText("별점 " + score);
        starIV1.setImageResource(R.drawable.star_fill);
        starIV2.setImageResource(R.drawable.star_fill);
        starIV3.setImageResource(R.drawable.star_fill);
        starIV4.setImageResource(R.drawable.star_fill);
        starIV5.setImageResource(R.drawable.star_fill);
    }

    @OnClick(R.id.bt_save_review)void saveButtonClick(){


        appDatabase.child("reviews").child(contentId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Date date = new Date(System.currentTimeMillis());

                        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                        String strDate = dateFormat.format(date);
                        Review review = new Review(MyApplication.getMyUid(), MyApplication.getMyName(), MyApplication.getMyPhotoUrl(), reviewEditText.getText().toString(), strDate, score);
                        if(dataSnapshot.exists()) {
                            ReviewAchievement reviewAchievement = dataSnapshot.getValue(ReviewAchievement.class);
                            reviewAchievement.addReviews(review);
                            reviewAchievement.setContentId(contentId);
                            try {
                                reviewAchievement.setStars((reviewAchievement.getStars() + score) / reviewAchievement.getReviews().size());
                            } catch (Exception ex) {
                                reviewAchievement.setStars(score);
                            }
                            appDatabase.child("reviews").child(contentId).setValue(reviewAchievement);
                        } else {
                            ReviewAchievement reviewAchievement = new ReviewAchievement();
                            reviewAchievement.setStars(score);
                            reviewAchievement.addReviews(review);
                            reviewAchievement.setContentId(contentId);
                            appDatabase.child("reviews").child(contentId).setValue(reviewAchievement);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGTAG, "database error = " + databaseError);
                    }
                });
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        appDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        contentId = intent.getExtras().getString("contentid");

        ActionBar ab = getSupportActionBar();
        ab.setTitle("리뷰 남기기");
    }
}
