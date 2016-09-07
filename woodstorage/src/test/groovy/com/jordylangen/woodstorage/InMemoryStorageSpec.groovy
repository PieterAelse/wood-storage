package com.jordylangen.woodstorage

import android.util.Log
import rx.functions.Action1

class InMemoryStorageSpec extends RxSpecification {

    InMemoryStorage storage;

    def setup() {
        storage = new InMemoryStorage()
    }

    def "should not contain more then max amount of items"() {
        when:
        for (def i = 0; i < InMemoryStorage.MAX_ITEMS * 2; i++) {
            storage.save(new LogEntry("test", Log.DEBUG, Integer.toString(i), null))
        }

        List<LogEntry> logs = [];
        storage.load()
                .subscribe(new Action1<LogEntry>() {
                    @Override
                    void call(LogEntry logStatement) {
                        logs.add(logStatement)
                    }
                })

        then:
        !logs.isEmpty()
        logs.size() == InMemoryStorage.MAX_ITEMS
        logs[0].message == Integer.toString(InMemoryStorage.MAX_ITEMS)
        logs[InMemoryStorage.MAX_ITEMS - 1].message == Integer.toString((InMemoryStorage.MAX_ITEMS * 2) - 1)
    }
}