package com.adostudio.adohi.adventour.db;

/**
 * Created by ADOHI on 2017-02-17.
 */

public class Achievement {
    private String contentId;
    private String contentTypeId;
    private String overview;
    private String title;
    private String address;
    private String phonecall;
    private String homepage;
    private String time;
    private double lng;
    private double lat;
    private String imageUrl;
    private double distance;

    public Achievement(){}
    public Achievement(String contentId, String contentTypeId, String overview, String title
    , String address, String phonecall, String homepage, String time, double lng, double lat, String imageUrl, double distance){
        this.contentId = contentId;
        this.contentTypeId = contentTypeId;
        this.overview = overview;
        this.title = title;
        this.address = address;
        this.phonecall = phonecall;
        this.homepage = homepage;
        this.time = time;
        this.lng = lng;
        this.lat = lat;
        this.imageUrl = imageUrl;
        this.distance = distance;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentTypeId() {
        return contentTypeId;
    }

    public void setContentTypeId(String contentTypeId) {
        this.contentTypeId = contentTypeId;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhonecall() {
        return phonecall;
    }

    public void setPhonecall(String phonecall) {
        this.phonecall = phonecall;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
