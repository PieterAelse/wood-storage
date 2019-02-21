package com.jordylangen.woodstorage.storage

import io.reactivex.Flowable

interface Storage {

    fun save(logEntry: LogEntry)

    fun load(): Flowable<LogEntry>?

    fun clear()
}
