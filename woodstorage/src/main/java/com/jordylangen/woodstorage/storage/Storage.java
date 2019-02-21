package com.jordylangen.woodstorage.storage;

import com.jordylangen.woodstorage.LogEntry;

import io.reactivex.Flowable;

public interface Storage {

    void save(LogEntry logEntry);

    Flowable<LogEntry> load();

    void clear();
}
