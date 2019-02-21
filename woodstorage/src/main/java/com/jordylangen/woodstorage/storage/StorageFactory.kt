package com.jordylangen.woodstorage.storage

import android.content.Context
import android.util.Log

import java.io.File

open class StorageFactory {

    companion object {
        private const val TAG = "StorageFactory"
        private const val STORAGE_DIRECTORY = "/logging"
        private const val STORAGE_FILE_NAME = "wood-storage.txt"
    }

    open fun create(context: Context?): Storage {
        val appStorageDirectory = context?.filesDir?.absolutePath

        try {
            val storageDirectory = File(appStorageDirectory, STORAGE_DIRECTORY)
            if (!storageDirectory.exists()) {
                storageDirectory.mkdirs()
            }

            val storageFile = File(storageDirectory.absolutePath, STORAGE_FILE_NAME)
            if (!storageFile.exists()) {
                storageFile.createNewFile()
            }

            return FileStorage(storageFile.absolutePath)
        } catch (exception: Exception) {
            Log.e(TAG, "could not create the required storage file, falling back to in memory storage", exception)
            return InMemoryStorage()
        }

    }
}
