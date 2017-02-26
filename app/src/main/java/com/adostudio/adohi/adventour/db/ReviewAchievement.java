package com.adostudio.adohi.adventour.db;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-23.
 */
@IgnoreExtraProperties
public class ReviewAchievement {

    private ArrayList<Review> reviews;
    private double stars;
    private String contentId;
    public ReviewAchievement(){
        stars = 0;
        reviews = new ArrayList<>();
        contentId = "";
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReviews(Review review) {
        this.reviews.add(0, review);
    }

    public double getStars() {
        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
}
