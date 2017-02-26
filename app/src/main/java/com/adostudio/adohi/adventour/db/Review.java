package com.adostudio.adohi.adventour.db;

/**
 * Created by ADOHI on 2017-02-23.
 */

public class Review {

    private String uid;
    private String name;
    private String imageUrl;
    private String review;

    private String time;
    private int star;

    public Review() {}
    public Review(String uid, String name, String imageUrl, String review, String time, int star) {
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;
        this.review = review;
        this.time = time;
        this.star = star;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }
}
