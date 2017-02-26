package com.adostudio.adohi.adventour.db;

import java.io.Serializable;

/**
 * Created by ADOHI on 2017-02-21.
 */

public class Sticker{

    private String assetName;
    private int resId;
    public Sticker(){

    }
    public Sticker(String assetName, int resId){
        this.assetName = assetName;
        this.resId = resId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
