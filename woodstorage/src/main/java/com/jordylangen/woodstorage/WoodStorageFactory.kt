package com.jordylangen.woodstorage

import com.jordylangen.woodstorage.storage.LogEntry
import com.jordylangen.woodstorage.storage.StorageFactory

import io.reactivex.processors.PublishProcessor
import java.io.File

class WoodStorageFactory {
    companion object {
        private val publishSubject = PublishProcessor.create<LogEntry>()
        private val tree = WoodStorageTree(publishSubject)

        @JvmStatic
        var worker: WoodStorageWorker? = null
            private set

        @JvmStatic
        fun getInstance(directory: File, storageFactory: StorageFactory = StorageFactory()): WoodStorageTree {
            stop()
            worker = WoodStorageWorker(storageFactory.create(directory), publishSubject).also { it.start() }

            return tree
        }

        @Synchronized
        @JvmStatic
        private fun stop() = worker?.stop()
    }
}
