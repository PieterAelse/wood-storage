package com.jordylangen.woodstorage;

import androidx.annotation.NonNull;
import io.reactivex.processors.PublishProcessor;
import timber.log.Timber;

public class WoodStorageTree extends Timber.DebugTree {

    private PublishProcessor<LogEntry> logEntryPublishProcessor;

    WoodStorageTree(PublishProcessor<LogEntry> logStatementPublishSubject) {
        this.logEntryPublishProcessor = logStatementPublishSubject;
    }

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        LogEntry logEntry = new LogEntry(tag, priority, message);
        logEntryPublishProcessor.onNext(logEntry);
    }
}
