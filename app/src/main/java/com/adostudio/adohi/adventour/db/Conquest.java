package com.adostudio.adohi.adventour.db;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by ADOHI on 2017-02-21.
 */

@IgnoreExtraProperties
public class Conquest {
    public String contentId;
    public String uid;
    public String name;
    public String imageUrl;
    public Conquest() {

    }
    public Conquest(String contentId, String uid, String name, String imageUrl) {
        this.contentId = contentId;
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;


    }
}
