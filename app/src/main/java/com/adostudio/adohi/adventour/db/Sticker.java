package com.adostudio.adohi.adventour.db;

import java.io.Serializable;

/**
 * Created by ADOHI on 2017-02-21.
 */

public class Sticker{

    public String assetName;
    public int resId;
    public Sticker(){

    }
    public Sticker(String assetName, int resId){
        this.assetName = assetName;
        this.resId = resId;
    }
}
