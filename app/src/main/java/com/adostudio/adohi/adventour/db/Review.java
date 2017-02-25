package com.adostudio.adohi.adventour.db;

/**
 * Created by ADOHI on 2017-02-23.
 */

public class Review {

    public String uid;
    public String name;
    public String imageUrl;
    public String review;

    public String time;
    public int star;

    public Review() {}
    public Review(String uid, String name, String imageUrl, String review, String time, int star) {
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;
        this.review = review;
        this.time = time;
        this.star = star;
    }

}
