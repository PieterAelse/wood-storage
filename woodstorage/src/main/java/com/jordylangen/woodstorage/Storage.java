package com.jordylangen.woodstorage;

import io.reactivex.Flowable;
import java.io.File;

public interface Storage {

    void save(LogEntry logEntry);

    Flowable<LogEntry> load();

    File copyToSDCard();

    void clear();
}
