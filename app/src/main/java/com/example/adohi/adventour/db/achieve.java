package com.example.adohi.adventour.db;

/**
 * Created by ADOHI on 2017-02-17.
 */

public class achieve {
    public String contentId;
    public String contentTypeId;
    public String overview;
    public String title;
    public String address;
    public String phonecall;
    public String homepage;
    public long time;
    public double lng;
    public double lat;
    public String imageUrl;
    public double distance;

    public achieve(){}
    public achieve(String contentId, String contentTypeId, String overview, String title
    , String address, String phonecall, String homepage, long time, double lng, double lat, String imageUrl, double distance){
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
}
