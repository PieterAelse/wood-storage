package com.jordylangen.woodstorage;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WoodStorageWorker implements Action1<LogEntry> {

    private Storage storage;
    private Observable<LogEntry> logObserver;
    private Subscription subscription;

    public WoodStorageWorker(Storage storage, Observable<LogEntry> logObserver) {
        this.storage = storage;
        this.logObserver = logObserver;
    }

    public void start() {
        subscription = logObserver.subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .subscribe(this);
    }

    public void stop() {
        subscription.unsubscribe();
    }

    @Override
    public void call(LogEntry logEntry) {
        storage.save(logEntry);
    }

    public Storage getStorage() {
        return storage;
    }
}
