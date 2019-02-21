package com.jordylangen.woodstorage.storage

import com.jordylangen.woodstorage.LogEntry

import java.util.ArrayList

import io.reactivex.Flowable

class InMemoryStorage : Storage {

    companion object {
        internal const val MAX_ITEMS = 128
    }

    private var logEntries: MutableList<LogEntry>

    init {
        logEntries = ArrayList()
    }

    override fun save(logEntry: LogEntry) {
        logEntries.add(logEntry)

        if (logEntries.size > MAX_ITEMS) {
            logEntries = logEntries.subList(logEntries.size - MAX_ITEMS, logEntries.size)
        }
    }

    override fun load(): Flowable<LogEntry> = Flowable.fromIterable(logEntries)

    override fun clear() = logEntries.clear()
}
