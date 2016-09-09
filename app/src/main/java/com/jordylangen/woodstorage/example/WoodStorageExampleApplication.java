package com.jordylangen.woodstorage.example;

import android.app.Application;

import com.jordylangen.woodstorage.WoodStorageFactory;

import timber.log.Timber;

public class WoodStorageExampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
        Timber.plant(WoodStorageFactory.getInstance(this));

        Timber.d("example application started");
    }

    @Override
    public void onTerminate() {
        Timber.d("example application stopped");
        super.onTerminate();
    }
}
