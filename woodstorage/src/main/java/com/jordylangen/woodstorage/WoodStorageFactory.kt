package com.jordylangen.woodstorage

import com.jordylangen.woodstorage.storage.LogEntry
import com.jordylangen.woodstorage.storage.StorageConfig
import com.jordylangen.woodstorage.storage.StorageFactory

import io.reactivex.processors.PublishProcessor
import java.io.File

class WoodStorageFactory {
    companion object {
        private const val MAX_LOG_COUNT = 1028
        private const val DELETE_COUNT = 256

        private val publishSubject = PublishProcessor.create<LogEntry>()
        private val tree = WoodStorageTree(publishSubject)

        @JvmStatic
        var worker: WoodStorageWorker? = null
            private set

        @JvmStatic
        fun getInstance(directory: File,
                        config: StorageConfig = StorageConfig(MAX_LOG_COUNT, DELETE_COUNT),
                        storageFactory: StorageFactory = StorageFactory()): WoodStorageTree {
            stop()

            worker = WoodStorageWorker(storageFactory.create(directory, config), publishSubject)
            worker!!.start()

            return tree
        }

        @Synchronized
        @JvmStatic
        private fun stop() = worker?.stop()
    }
}
