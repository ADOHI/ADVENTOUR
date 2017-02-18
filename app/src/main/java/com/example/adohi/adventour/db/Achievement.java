package com.example.adohi.adventour.db;

import android.location.Location;

/**
 * Created by ADOHI on 2017-02-17.
 */

public class Achievement {
    public String id;
    public long time;
    public double lng;
    public double lat;



    public Achievement() {
        this.id = null;
        this.time = -1;
        this.lat = -1;
        this.lng = -1;


    }

    public Achievement(String id, long time, double lng, double lat) {
        this.id = id;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
    }
}
