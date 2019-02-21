package com.jordylangen.woodstorage.storage

import timber.log.Timber
import java.io.File

open class StorageFactory {

    companion object {
        private const val STORAGE_DIRECTORY = "/logging"
        private const val STORAGE_FILE_NAME = "wood-storage.txt"
    }

    open fun create(directory: File, config: StorageConfig): Storage {
        try {
            val storageDirectory = File(directory, STORAGE_DIRECTORY)
            if (!storageDirectory.exists()) {
                storageDirectory.mkdirs()
            }

            val storageFile = File(storageDirectory.absolutePath, STORAGE_FILE_NAME)
            if (!storageFile.exists()) {
                storageFile.createNewFile()
            }

            return FileStorage(storageFile.absolutePath, config)
        } catch (exception: Exception) {
            Timber.e(exception, "could not create the required storage file, falling back to in memory storage")
            return InMemoryStorage()
        }
    }
}
