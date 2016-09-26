package com.jordylangen.woodstorage

import android.os.Environment
import android.util.Log
import mockit.Mock
import mockit.MockUp
import io.reactivex.functions.Consumer
import io.reactivex.subscribers.TestSubscriber
import org.junit.Rule
import org.junit.rules.TestName

class FileStorageSpec extends RxSpecification {

    private static final String PATH_TO_DIRECTORY = "build/tmp/"
    @Rule
    TestName name = new TestName()

    def createFileForCurrentTest() {
        def fileName = name.methodName + ".txt"
        def pathToFile = PATH_TO_DIRECTORY + fileName
        createFileForCurrentTest(pathToFile)
        return pathToFile
    }

    def createFileForCurrentTest(String pathToFile) {
        def directory = new File(PATH_TO_DIRECTORY)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        def file = new File(pathToFile)

        if (file.exists()) {
            file.delete()
        }

        file.createNewFile()
    }

    def "should write the log to the text file"() {
        given:
        def pathToFile = createFileForCurrentTest()
        def fileStorage = new FileStorage(new StorageConfig(10, 2, pathToFile))
        def log = new LogEntry("fileStorageSpec", Log.DEBUG, "first log")

        when:
        fileStorage.save(log)

        List<LogEntry> logs = []
        fileStorage.load()
                .subscribe(new Consumer<LogEntry>() {
            @Override
            void accept(LogEntry logStatement) {
                logs.add(logStatement)
            }
        })

        then:
        !logs.isEmpty()
        logs.size() == 1
        logs[0].equals(log)
    }

    def "should not write more logs then the specified max, and delete the required amount of logs"() {
        given:
        def pathToFile = createFileForCurrentTest()

        def maxCount = 10
        def deleteCount = 4
        def fileStorage = new FileStorage(new StorageConfig(maxCount, deleteCount, pathToFile))

        when:
        for (def index = 0; index < maxCount + 1; index++) {
            fileStorage.save(new LogEntry("spec", 0, Integer.toString(index)))
        }

        List<LogEntry> logs = []
        fileStorage.load()
                .subscribe(new Consumer<LogEntry>() {
            @Override
            void accept(LogEntry logStatement) {
                logs.add(logStatement)
            }
        })

        then:
        !logs.isEmpty()
        logs.size() == maxCount - deleteCount
    }

    def "should report new logs to the observer after loading"() {
        given:
        def pathToFile = createFileForCurrentTest()

        def fileStorage = new FileStorage(new StorageConfig(10, 2, pathToFile))
        def log = new LogEntry("fileStorageSpec", Log.DEBUG, "first log")

        when:
        fileStorage.save(log)

        List<LogEntry> logStatements = []
        fileStorage.load()
                .subscribe(new Consumer<LogEntry>() {
            @Override
            void accept(LogEntry logStatement) {
                logStatements.add(logStatement)
            }
        })

        then:
        !logStatements.isEmpty()
        logStatements.size() == 1
        logStatements[0].equals(log)

        when:
        fileStorage.save(log)

        then:
        logStatements.size() == 2
    }

    def "should clear log file entries"() {
        given:
        def fileStorage = new FileStorage(new StorageConfig(42, 23, createFileForCurrentTest()))

        when:
        fileStorage.save(new LogEntry("tag", 0, "u no see this"))
        fileStorage.load().subscribe(new TestSubscriber<LogEntry>())

        and:
        fileStorage.clear()

        List<LogEntry> logs = []
        fileStorage.load()
                .subscribe(new Consumer<LogEntry>() {
            @Override
            void accept(LogEntry logStatement) {
                logs.add(logStatement)
            }
        })

        then:
        logs.isEmpty()
    }

    def "should copy log file to SD storage"() {
        given:
        def pathToFile = createFileForCurrentTest()
        def maxCount = 10
        def deleteCount = 4
        def fileStorage = new FileStorage(new StorageConfig(maxCount, deleteCount, pathToFile))
        new MockUp<Environment>() {
            @Mock
            File getExternalStorageDirectory() {
                return new File(PATH_TO_DIRECTORY + "/sdcard/")
            }
        }

        when:
        for (def index = 0; index < maxCount + 1; index++) {
            fileStorage.save(new LogEntry("spec", 0, Integer.toString(index)))
        }

        and:
        def originalFileCount = fileStorage.getLineCount()
        def copiedFile = fileStorage.copyToSDCard()
        fileStorage.file = copiedFile
        def copiedFileLineCount = fileStorage.getLineCount()

        then:
        copiedFile != null
        copiedFile.exists()
        originalFileCount == copiedFileLineCount
    }
}