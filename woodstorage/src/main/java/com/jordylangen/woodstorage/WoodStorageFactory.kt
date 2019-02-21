package com.jordylangen.woodstorage

import android.content.Context

import com.jordylangen.woodstorage.storage.StorageFactory

import io.reactivex.processors.PublishProcessor

class WoodStorageFactory {
    companion object {
        @JvmStatic
        var worker: WoodStorageWorker? = null
            private set

        @JvmOverloads @JvmStatic
        fun getInstance(context: Context?, storageFactory: StorageFactory = StorageFactory()): WoodStorageTree {
            val publishSubject = PublishProcessor.create<LogEntry>()
            val tree = WoodStorageTree(publishSubject)

            worker?.stop()

            worker = WoodStorageWorker(storageFactory.create(context), publishSubject).also { it.start() }

            return tree
        }
    }



    @Synchronized
    private fun stop() = worker?.stop()
}
