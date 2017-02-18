package com.example.adohi.adventour.db;

import android.location.Location;

/**
 * Created by ADOHI on 2017-02-17.
 */

public class Bookmark {
    public String id;
    public double lng;
    public double lat;

    public Bookmark() {
        this.id = null;
        this.lat = -1;
        this.lng = -1;

    }

    public Bookmark(String id, double lng, double lat) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }


}