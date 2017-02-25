package com.adostudio.adohi.adventour.db;

import android.graphics.Bitmap;

import com.google.firebase.database.IgnoreExtraProperties;
import com.vuforia.DataSet;
import com.vuforia.Trackable;

/**
 * Created by ADOHI on 2017-02-22.
 */
@IgnoreExtraProperties
public class Test {
    public DataSet picture;
    public Test(){picture = null;}
    public Test(DataSet picture){
        this.picture = picture;
    }
}
