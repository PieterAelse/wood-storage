package com.jordylangen.woodstorage.storage

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.LineNumberReader
import java.util.ArrayList

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.ReplayProcessor
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class FileStorage(pathToFile: String, private val storageConfig: StorageConfig) : Storage {

    companion object {
        private const val DELETE_OFFSET_BY_INDEX_AND_NEW_WRITE = 2
    }

    private val file: File = File(pathToFile)
    private var replayProcessor: ReplayProcessor<LogEntry>? = null
    private var disposable: Disposable? = null

    private val lineCount: Int
        @Synchronized get() {
            try {
                val fileReader = FileReader(file)
                val lineNumberReader = LineNumberReader(fileReader)
                while (lineNumberReader.readLine() != null);
                val lineCount = lineNumberReader.lineNumber
                lineNumberReader.close()

                return lineCount
            } catch (exception: IOException) {
                Timber.e(exception, "could not get the line count for ${file.absolutePath}")
                return -1
            }
        }

    @Synchronized
    override fun save(logEntry: LogEntry) {
        val lineCount = lineCount
        ensureMaxLineCount(lineCount)
        write(logEntry)

        replayProcessor?.onNext(logEntry)
    }

    @Synchronized
    private fun ensureMaxLineCount(currentLineCount: Int) {
        if (currentLineCount < storageConfig.maxLogCount) {
            return
        }

        val startAtLine = currentLineCount - (storageConfig.maxLogCount - storageConfig.deleteCount - DELETE_OFFSET_BY_INDEX_AND_NEW_WRITE)
        val stringBuffer = StringBuilder()

        try {
            var lineCounter = 0

            val fileReader = FileReader(file)
            val reader = BufferedReader(fileReader)

            var line = reader.readLine()
            while (line != null) {
                lineCounter++

                if (lineCounter >= startAtLine) {
                    stringBuffer.append(line)
                    stringBuffer.append("\n")
                }
                line = reader.readLine()
            }

            reader.close()

            val fileWriter = FileWriter(file)
            val out = BufferedWriter(fileWriter)

            // do we need to optimize this? write a set of chars?
            out.write(stringBuffer.toString())

            out.flush()
            out.close()

        } catch (exception: IOException) {
            Timber.e(exception, "could not trim the file ${file.absolutePath}")
        }

    }

    @Synchronized
    private fun write(logEntry: LogEntry) {
        try {
            val fileWriter = FileWriter(file, true)
            val out = BufferedWriter(fileWriter)
            out.write(logEntry.serialize())
            out.write("\n")
            out.flush()
            out.close()
        } catch (exception: IOException) {
            Timber.e(exception, "could not write to file at ${file.absolutePath}")
        }

    }

    override fun load(): Flowable<LogEntry>? {
        if (replayProcessor == null) {
            replayProcessor = ReplayProcessor.create()

            disposable = Observable.fromCallable { loadLogsFromFile() }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { logEntries -> Observable.fromIterable(logEntries) }
                .subscribe { logEntry -> replayProcessor!!.onNext(logEntry) }

        }

        return replayProcessor
    }

    override fun clear() {
        try {
            disposable?.dispose()
            replayProcessor?.onComplete()
            replayProcessor = null

            if (file.delete()) {
                file.createNewFile()
            } else {
                // for some reason we cannot delete the file (access rights)
                // so we just write "nothing" to the file, same end result
                val fileWriter = FileWriter(file, false)
                fileWriter.close()
            }
        } catch (exception: IOException) {
            Timber.e(exception, "could not write to file at ${file.absolutePath}")
        }

    }

    private fun loadLogsFromFile(): List<LogEntry> {
        val logs = ArrayList<LogEntry>()

        try {
            val fileReader = FileReader(file)
            val reader = BufferedReader(fileReader)

            var line = reader.readLine()
            while (line != null) {
                val logEntry = LogEntry.deserialize(line)
                logs.add(logEntry)
                line = reader.readLine()
            }

            reader.close()
        } catch (exception: IOException) {
            Timber.e(exception, "could not read from file at ${file.absolutePath}")
            return ArrayList()
        }

        return logs
    }
}
