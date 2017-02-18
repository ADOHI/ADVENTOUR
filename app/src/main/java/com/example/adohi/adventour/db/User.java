package com.example.adohi.adventour.db;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-15.
 */

@IgnoreExtraProperties
public class User {

    public String userName;
    public String email;

    public ArrayList<achieve> bookmarkList;
    public ArrayList<achieve> achievementList;
    public ArrayList<String> friendList;

    public User() {
        friendList = new ArrayList<>();
        achievementList = new ArrayList<>();
        bookmarkList = new ArrayList<>();
    }


    public User(String userName) {
        this.userName = userName;

    }

    public boolean checkBookmarkId(String id){
        for(achieve a : bookmarkList){
            if(a.contentId.equals(id))return true;
        }
        return false;
    }

    public boolean checkAchievementId(String id){
        for(achieve a : achievementList){
            if(a.contentId.equals(id))return true;
        }
        return false;
    }

    public void removeBookmark(String id){
        int index = 0;
        for(achieve a : bookmarkList){
            Log.d("chhhh", ""+index);
            if(a.contentId.equals(id)){
                bookmarkList.remove(index++);
                break;
            }
        }
    }


}
