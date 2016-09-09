package com.jordylangen.woodstorage;

import rx.subjects.PublishSubject;
import timber.log.Timber;

public class WoodStorageTree extends Timber.DebugTree {

    private PublishSubject<LogEntry> logStatementPublishSubject;

    WoodStorageTree(PublishSubject<LogEntry> logStatementPublishSubject) {
        this.logStatementPublishSubject = logStatementPublishSubject;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        LogEntry logEntry = new LogEntry(tag, priority, message);
        logStatementPublishSubject.onNext(logEntry);
    }
}
