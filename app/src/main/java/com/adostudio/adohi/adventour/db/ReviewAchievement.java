package com.adostudio.adohi.adventour.db;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-23.
 */
@IgnoreExtraProperties
public class ReviewAchievement {

    public ArrayList<Review> reviews;
    public double stars;
    public String contentId;
    public ReviewAchievement(){
        stars = 0;
        reviews = new ArrayList<>();
        contentId = "";
    }

}
