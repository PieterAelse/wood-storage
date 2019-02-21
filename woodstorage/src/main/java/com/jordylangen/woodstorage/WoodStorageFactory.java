package com.jordylangen.woodstorage;

import android.content.Context;

import com.jordylangen.woodstorage.storage.StorageFactory;

import io.reactivex.processors.PublishProcessor;

public final class WoodStorageFactory {

    private static WoodStorageWorker WORKER;

    public static WoodStorageTree getInstance(Context context) {
        return getInstance(context, new StorageFactory());
    }

    public static WoodStorageTree getInstance(Context context, StorageFactory storageFactory) {
        PublishProcessor<LogEntry> publishSubject = PublishProcessor.create();
        WoodStorageTree tree = new WoodStorageTree(publishSubject);

        if (WORKER != null) {
            stop();
        }

        WORKER = new WoodStorageWorker(storageFactory.create(context), publishSubject);
        WORKER.start();

        return tree;
    }

    private static synchronized void stop() {
        WORKER.stop();
    }

    public static WoodStorageWorker getWorker() {
        return WORKER;
    }
}
