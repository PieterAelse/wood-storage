package com.jordylangen.woodstorage;

import java.io.File;

import rx.Observable;

public interface Storage {

    void save(LogEntry logEntry);

    Observable<LogEntry> load();

    File copyToSDCard();

    void clear();
}
