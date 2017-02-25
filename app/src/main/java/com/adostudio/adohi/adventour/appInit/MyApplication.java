//realm 초기화

package com.adostudio.adohi.adventour.appInit;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;

public class MyApplication extends Application {
    private DatabaseReference myDataBase;

    private String myUid;
    private String myPhotoUrl;
    private String myName;

    private String issueFriendImageUrl;
    private String issueFriendName;
    private String issueFriendUid;
    private String issueLocationName;
    private double currentLng;
    private double currentLat;

    private int stickerPosition;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        myDataBase = FirebaseDatabase.getInstance().getReference();

        setDefaultFont(this, "DEFAULT", "font_second.ttf");
        setDefaultFont(this, "SANS_SERIF", "font_second.ttf");
        setDefaultFont(this, "SERIF", "font_second.ttf");
        myUid = null;
        myPhotoUrl = null;
        myName = null;
        issueFriendImageUrl = null;
        issueFriendName = null;
        issueFriendUid = null;
        issueLocationName = null;
        currentLng = 0;
        currentLat = 0;
        stickerPosition = -1;
    }

    public static void setDefaultFont(Context ctx,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(ctx.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
        }

    protected static void replaceFont(String staticTypefaceFieldName,
                                                  final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class
            .getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
            } catch (NoSuchFieldException e) {
            e.printStackTrace();
            } catch (IllegalAccessException e) {
                Log.d("aa", "aa");
            }
        }

    public DatabaseReference getMyDataBase() {
        return myDataBase;
    }

    public String getIssueFriendImageUrl() {
        return issueFriendImageUrl;
    }

    public void setIssueFriendImageUrl(String issueFriendImageUrl) {
        this.issueFriendImageUrl = issueFriendImageUrl;
    }

    public String getIssueFriendName() {
        return issueFriendName;
    }

    public void setIssueFriendName(String issueFriendName) {
        this.issueFriendName = issueFriendName;
    }

    public String getIssueFriendUid() {
        return issueFriendUid;
    }

    public void setIssueFriendUid(String issueFriendUid) {
        this.issueFriendUid = issueFriendUid;
    }

    public String getIssueLocationName() {
        return issueLocationName;
    }

    public void setIssueLocationName(String issueLocationName) {
        this.issueLocationName = issueLocationName;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getMyPhotoUrl() {
        return myPhotoUrl;
    }

    public void setMyPhotoUrl(String myPhotoUrl) {
        this.myPhotoUrl = myPhotoUrl;
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public int getStickerPosition() {
        return stickerPosition;
    }

    public void setStickerPosition(int stickerPosition) {
        this.stickerPosition = stickerPosition;
    }
}
