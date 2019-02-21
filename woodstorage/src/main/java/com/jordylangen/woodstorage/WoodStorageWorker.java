package com.jordylangen.woodstorage;

import com.jordylangen.woodstorage.storage.Storage;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WoodStorageWorker {

    private Storage storage;
    private Flowable<LogEntry> logObserver;
    private Disposable subscription;

    WoodStorageWorker(Storage storage, Flowable<LogEntry> logObserver) {
        this.storage = storage;
        this.logObserver = logObserver;
    }

    void start() {
        subscription = logObserver.subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<LogEntry>() {
                    @Override
                    public void accept(LogEntry logEntry) {
                        storage.save(logEntry);
                    }
                });
    }

    void stop() {
        subscription.dispose();
    }

    public Storage getStorage() {
        return storage;
    }
}
