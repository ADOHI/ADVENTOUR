package com.adostudio.adohi.adventour.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.vuforia.DataSet;

import java.io.Serializable;

/**
 * Created by ADOHI on 2017-02-19.
 */
public class Quest{

    private Sticker reward;
    private String fromUid;
    private String fromName;
    private String fromImageUrl;
    private String toUid;
    private String toName;
    private String toImageUrl;
    private String locationName;
    private String locationHint;
    private double locationLng;
    private double locationLat;

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

    public Sticker getReward() {
        return reward;
    }

    public void setReward(Sticker reward) {
        this.reward = reward;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromImageUrl() {
        return fromImageUrl;
    }

    public void setFromImageUrl(String fromImageUrl) {
        this.fromImageUrl = fromImageUrl;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getToImageUrl() {
        return toImageUrl;
    }

    public void setToImageUrl(String toImageUrl) {
        this.toImageUrl = toImageUrl;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationHint() {
        return locationHint;
    }

    public void setLocationHint(String locationHint) {
        this.locationHint = locationHint;
    }

    public double getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(double locationLng) {
        this.locationLng = locationLng;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }
}
