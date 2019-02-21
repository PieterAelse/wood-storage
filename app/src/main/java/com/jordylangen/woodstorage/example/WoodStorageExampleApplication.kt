package com.jordylangen.woodstorage.example

import android.app.Application

import com.jordylangen.woodstorage.WoodStorageFactory

import timber.log.Timber

class WoodStorageExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        //val config = StorageConfig(1024, 128)
        Timber.plant(WoodStorageFactory.getInstance(applicationContext.filesDir))

        Timber.d("example application started")
    }

    override fun onTerminate() {
        Timber.d("example application stopped")
        super.onTerminate()
    }
}
