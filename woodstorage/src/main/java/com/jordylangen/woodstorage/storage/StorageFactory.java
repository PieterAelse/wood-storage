package com.jordylangen.woodstorage.storage;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class StorageFactory {

    private static final String TAG = "StorageFactory";
    private static final String STORAGE_DIRECTORY = "/logging";
    private static final String STORAGE_FILE_NAME = "wood-storage.txt";

    public Storage create(Context context) {
        String appStorageDirectory = context.getFilesDir().getAbsolutePath();

        try {
            File storageDirectory = new File(appStorageDirectory, STORAGE_DIRECTORY);
            if (!storageDirectory.exists()) {
                storageDirectory.mkdirs();
            }

            File storageFile = new File(storageDirectory.getAbsolutePath(), STORAGE_FILE_NAME);
            if (!storageFile.exists()) {
                storageFile.createNewFile();
            }

            return new FileStorage(storageFile.getAbsolutePath());
        } catch (Exception exception) {
            Log.e(TAG, "could not create the required storage file, falling back to in memory storage", exception);
            return new InMemoryStorage();
        }
    }
}
