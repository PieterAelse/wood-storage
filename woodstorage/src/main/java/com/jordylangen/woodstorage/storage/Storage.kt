package com.jordylangen.woodstorage.storage

import com.jordylangen.woodstorage.LogEntry

import io.reactivex.Flowable

interface Storage {

    fun save(logEntry: LogEntry)

    fun load(): Flowable<LogEntry>?

    fun clear()
}
