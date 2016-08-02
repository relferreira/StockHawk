package com.sam_chordas.android.stockhawk;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by relferreira on 8/1/16.
 */
public class StockHawkApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
