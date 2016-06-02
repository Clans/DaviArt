package com.github.clans.daviart;

import android.app.Application;

import timber.log.Timber;

public class DeviArtApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
