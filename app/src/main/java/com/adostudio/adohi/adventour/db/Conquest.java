package com.adostudio.adohi.adventour.db;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by ADOHI on 2017-02-21.
 */

@IgnoreExtraProperties
public class Conquest {
    private String contentId;
    private String uid;
    private String name;
    private String imageUrl;
    public Conquest() {

    }
    public Conquest(String contentId, String uid, String name, String imageUrl) {
        this.contentId = contentId;
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;


    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
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
}
