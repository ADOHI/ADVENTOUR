package com.adostudio.adohi.adventour.db;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-15.
 */

@IgnoreExtraProperties
public class User {

    public String userName;
    public String photoUrl;
    public int score;
    public String memo;
    public String uid;
    public int flag;

    public ArrayList<Achievement> bookmarkList;
    public ArrayList<Achievement> achievementList;
    public ArrayList<Achievement> falgList;
    public ArrayList<String> friendList;
    public ArrayList<Sticker> stickerList;

    public User() {
        this.friendList = new ArrayList<>();
        this.achievementList = new ArrayList<>();
        this.bookmarkList = new ArrayList<>();
        this.falgList = new ArrayList<>();
        this.stickerList = new ArrayList<>();
        this.photoUrl = null;
        this.score = 0;
        this.memo = "";
        this.uid = "";
        this.flag = 0;
    }


    public User(String userName) {
        this.userName = userName;

    }

    public boolean checkBookmarkId(String id){
        for(Achievement a : bookmarkList){
            if(a.contentId.equals(id))return true;
        }
        return false;
    }

    public boolean checkAchievementId(String id){
        for(Achievement a : achievementList){
            if(a.contentId.equals(id))return true;
        }
        return false;
    }

    public void removeBookmark(String id){
        int index = 0;
        for(Achievement a : bookmarkList){
            if(a.contentId.equals(id)){
                bookmarkList.remove(index++);
                break;
            }
        }
    }


}
