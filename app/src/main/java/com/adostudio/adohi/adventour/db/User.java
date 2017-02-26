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

    private String userName;
    private String photoUrl;
    private String memo;
    private String uid;

    private ArrayList<Achievement> bookmarkList;
    private ArrayList<Achievement> achievementList;
    private ArrayList<Achievement> flagList;
    private ArrayList<String> friendList;
    private ArrayList<Sticker> stickerList;
    private ArrayList<Quest> questList;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<Achievement> getBookmarkList() {
        return bookmarkList;
    }

    public void setBookmarkList(ArrayList<Achievement> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    public void addBookmarkList(Achievement bookmark) {
        this.bookmarkList.add(0, bookmark);
    }

    public ArrayList<Achievement> getAchievementList() {
        return achievementList;
    }

    public void setAchievementList(ArrayList<Achievement> achievementList) {
        this.achievementList = achievementList;
    }

    public ArrayList<Achievement> getFlagList() {
        return flagList;
    }

    public void setFlagList(ArrayList<Achievement> flagList) {
        this.flagList = flagList;
    }

    public void addFlagList(Achievement flag) {
        this.flagList.add(0, flag);
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
    }

    public void addFriendList(String friend) {
        this.friendList.add(friend);
    }

    public void removeFriendList(String friend) {
        this.friendList.remove(friend);
    }

    public ArrayList<Sticker> getStickerList() {
        return stickerList;
    }

    public void setStickerList(ArrayList<Sticker> stickerList) {
        this.stickerList = stickerList;
    }

    public void addStickerList(Sticker sticker) {
        this.stickerList.add(sticker);
    }

    public void removeStickerList(int position) {
        this.stickerList.remove(position);
    }

    public ArrayList<Quest> getQuestList() {
        return questList;
    }

    public void setQuestList(ArrayList<Quest> questList) {
        this.questList = questList;
    }

    public void addQuestList(Quest quest) {
        this.questList.add(0, quest);
    }

    public boolean checkBookmarkId(String id){
        for(Achievement a : bookmarkList){
            if(a.getContentId().equals(id))return true;
        }
        return false;
    }

    public boolean checkAchievementId(String id){
        for(Achievement a : achievementList){
            if(a.getContentId().equals(id))return true;
        }
        return false;
    }

    public void removeBookmark(String id){
        int index = 0;
        for(Achievement a : bookmarkList){
            if(a.getContentId().equals(id)){
                bookmarkList.remove(index);
                break;
            } else index++;
        }
    }

    public void removeFlag(String id){
        int index = 0;
        for(Achievement a : bookmarkList){
            if(a.getContentId().equals(id)){
                flagList.remove(index);
                break;
            } else index++;

        }
    }

    public void removeQuest(double lng, double lat){
        int index = 0;
        for(Quest q : questList){
            if(q.getLocationLat() == lat && q.getLocationLng() == lng){
                questList.remove(index);
                break;
            } else index++;

        }
    }


}
