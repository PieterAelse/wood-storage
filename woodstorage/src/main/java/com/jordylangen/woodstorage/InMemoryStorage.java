package com.jordylangen.woodstorage;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

public class InMemoryStorage implements Storage {

    static final int MAX_ITEMS = 128;

    private List<LogEntry> logEntries;

    public InMemoryStorage() {
        logEntries = new ArrayList<>();
    }

    @Override
    public void save(LogEntry logEntry) {
        logEntries.add(logEntry);

        if (logEntries.size() > MAX_ITEMS) {
            logEntries = logEntries.subList(logEntries.size() - MAX_ITEMS, logEntries.size());
        }
    }

    @Override
    public Flowable<LogEntry> load() {
        return Flowable.fromIterable(logEntries);
    }

    @Override
    public void clear() {
        logEntries.clear();
    }
}
