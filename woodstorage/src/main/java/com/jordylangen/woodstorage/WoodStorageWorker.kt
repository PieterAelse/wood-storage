package com.jordylangen.woodstorage

import com.jordylangen.woodstorage.storage.LogEntry
import com.jordylangen.woodstorage.storage.Storage

import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WoodStorageWorker internal constructor(val storage: Storage, private val logObserver: Flowable<LogEntry>) {
    private var subscription: Disposable? = null

    internal fun start() {
        subscription = logObserver.subscribeOn(Schedulers.io())
            .onBackpressureBuffer()
            .observeOn(Schedulers.io())
            .subscribe { logEntry -> storage.save(logEntry) }
    }

    internal fun stop() = subscription?.dispose()
}
