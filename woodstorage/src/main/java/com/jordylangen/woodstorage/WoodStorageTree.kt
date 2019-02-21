package com.jordylangen.woodstorage

import io.reactivex.processors.PublishProcessor
import timber.log.Timber

class WoodStorageTree internal constructor(private val logEntryPublishProcessor: PublishProcessor<LogEntry>) :
    Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val logEntry = LogEntry(tag!!, priority, message)
        logEntryPublishProcessor.onNext(logEntry)
    }
}
