package com.example.adohi.adventour.db;

import com.google.firebase.database.IgnoreExtraProperties;
import com.nhn.android.maps.maplib.NGeoPoint;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ADOHI on 2017-02-15.
 */

@IgnoreExtraProperties
public class User {

    public String userName;
    public String email;

    public ArrayList<String> bookmarkIdList;
    public Map<String, NGeoPoint> bookmarkexample;
    public ArrayList<String> achievementIdList;
    public ArrayList<String> friendList;

    public User() {
        bookmarkIdList = new ArrayList<>();
        achievementIdList = new ArrayList<>();
        friendList = new ArrayList<>();
    }


    public User(String userName) {
        this.userName = userName;

    }

}
