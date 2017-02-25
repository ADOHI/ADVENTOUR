package com.adostudio.adohi.adventour.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.vuforia.DataSet;

import java.io.Serializable;

/**
 * Created by ADOHI on 2017-02-19.
 */
public class Quest{

    public Sticker reward;
    public String fromUid;
    public String fromName;
    public String fromImageUrl;
    public String toUid;
    public String toName;
    public String toImageUrl;
    public String locationName;
    public String locationHint;
    public double locationLng;
    public double locationLat;

    public Quest() {}
    public Quest(Sticker reward, String fromUid, String fromName, String fromImageUrl, String toUid,
                 String toName, String toImageUrl, String locationName, String locationHint,
                 double locationLng, double locationLat) {
        this.reward = reward;
        this.fromUid = fromUid;
        this.fromName = fromName;
        this.fromImageUrl = fromImageUrl;
        this.toUid = toUid;
        this.toName = toName;
        this.toImageUrl = toImageUrl;
        this.locationName = locationName;
        this.locationHint = locationHint;
        this.locationLng = locationLng;
        this.locationLat = locationLat;
    }

}
