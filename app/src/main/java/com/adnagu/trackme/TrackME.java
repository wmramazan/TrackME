package com.adnagu.trackme;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by wmramazan on 31.05.2017.
 */

public class TrackME extends Application {

    private static TrackME instance;
    static Database db;
    static SharedPreferences pref;
    static SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = new Database(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();

        Log.d("ADN", "TrackME.java onCreate()");
    }

    public static synchronized TrackME get() {
        return instance;
    }
}
