//realm 초기화

package com.example.adohi.adventour.AppInit;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.nhn.android.maps.NMapLocationManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
