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
    public String memo;
    public String uid;

    public ArrayList<Achievement> bookmarkList;
    public ArrayList<Achievement> achievementList;
    public ArrayList<Achievement> flagList;
    public ArrayList<String> friendList;
    public ArrayList<Sticker> stickerList;
    public ArrayList<Quest> questList;
    public User() {
        this.friendList = new ArrayList<>();
        this.achievementList = new ArrayList<>();
        this.bookmarkList = new ArrayList<>();
        this.flagList = new ArrayList<>();
        this.stickerList = new ArrayList<>();
        this.questList = new ArrayList<>();
        this.photoUrl = null;
        this.memo = "";
        this.uid = "";
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
                bookmarkList.remove(index);
                break;
            } else index++;
        }
    }

    public void removeFlag(String id){
        int index = 0;
        for(Achievement a : bookmarkList){
            if(a.contentId.equals(id)){
                flagList.remove(index);
                break;
            } else index++;

        }
    }

    public void removeQuest(double lng, double lat){
        int index = 0;
        for(Quest q : questList){
            if(q.locationLat == lat && q.locationLng == lng){
                questList.remove(index);
                break;
            } else index++;

        }
    }


}
