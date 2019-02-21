package com.jordylangen.woodstorage

import android.util.Log
import com.jordylangen.woodstorage.storage.InMemoryStorage
import io.reactivex.functions.Consumer

class InMemoryStorageSpec extends RxSpecification {

    InMemoryStorage storage;

    def setup() {
        storage = new InMemoryStorage()
    }

    def "should not contain more then max amount of items"() {
        when:
        for (def i = 0; i < InMemoryStorage.MAX_ITEMS * 2; i++) {
            storage.save(new LogEntry("test", Log.DEBUG, Integer.toString(i)))
        }

        List<LogEntry> logs = [];
        storage.load().subscribe(new Consumer<LogEntry>() {
            @Override
            void accept(LogEntry logStatement) {
                logs.add(logStatement)
            }
        })

        then:
        !logs.isEmpty()
        logs.size() == InMemoryStorage.MAX_ITEMS
        logs[0].message == Integer.toString(InMemoryStorage.MAX_ITEMS)
        logs[InMemoryStorage.MAX_ITEMS - 1].message == Integer.toString((InMemoryStorage.MAX_ITEMS * 2) - 1)
    }

    def "should clear logs"() {
        when:
        storage.save(new LogEntry("test", Log.ASSERT, "some log entry"))

        and:
        storage.clear()

        List<LogEntry> logs = [];
        storage.load().subscribe(new Consumer<LogEntry>() {
            @Override
            void accept(LogEntry logStatement) {
                logs.add(logStatement)
            }
        })

        then:
        logs.empty
    }
}