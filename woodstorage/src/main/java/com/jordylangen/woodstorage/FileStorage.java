package com.jordylangen.woodstorage;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.ReplayProcessor;
import io.reactivex.schedulers.Schedulers;

public class FileStorage implements Storage {

    private static final String TAG = "FileStorage";
    private static final int MAX_LOG_COUNT = 1028;
    private static final int DELETE_COUNT = 256;
    private static final int DELETE_OFFSET_BY_INDEX_AND_NEW_WRITE = 2;

    private File file;
    private StorageConfig storageConfig;
    private ReplayProcessor<LogEntry> replayProcessor;

    FileStorage(String pathToFile) {
        this(new StorageConfig(MAX_LOG_COUNT, DELETE_COUNT, pathToFile));
    }

    public FileStorage(StorageConfig storageConfig) {
        file = new File(storageConfig.getPathToFile());
        this.storageConfig = storageConfig;
    }

    @Override
    public synchronized void save(LogEntry logEntry) {
        int lineCount = getLineCount();
        ensureMaxLineCount(lineCount);
        write(file, logEntry);

        if (replayProcessor != null) {
            replayProcessor.onNext(logEntry);
        }
    }

    private synchronized int getLineCount() {
        try {
            FileReader fileReader = new FileReader(file);
            LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
            while ((lineNumberReader.readLine()) != null) ;
            int lineCount = lineNumberReader.getLineNumber();
            lineNumberReader.close();
            return lineCount;
        } catch (IOException exception) {
            Log.e(TAG, "could not get the line count for " + file.getAbsolutePath(), exception);
            return -1;
        }
    }

    private synchronized void ensureMaxLineCount(int currentLineCount) {
        if (currentLineCount < storageConfig.getMaxLogCount()) {
            return;
        }

        int startAtLine = currentLineCount - (storageConfig.getMaxLogCount() - storageConfig.getDeleteCount() - DELETE_OFFSET_BY_INDEX_AND_NEW_WRITE);
        StringBuilder stringBuffer = new StringBuilder();

        try {
            int lineCounter = 0;

            FileReader fileReader = new FileReader(file);
            BufferedReader in = new BufferedReader(fileReader);

            String line;
            while ((line = in.readLine()) != null) {
                lineCounter++;

                if (lineCounter >= startAtLine) {
                    stringBuffer.append(line);
                    stringBuffer.append("\n");
                }
            }

            in.close();

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fileWriter);

            // do we need to optimize this? write a set of chars?
            out.write(stringBuffer.toString());

            out.flush();
            out.close();

        } catch (IOException exception) {
            Log.e(TAG, "could not trim the file " + file.getAbsolutePath(), exception);
        }
    }

    private synchronized void write(File fileToWriteTo, LogEntry logEntry) {
        try {
            FileWriter fileWriter = new FileWriter(fileToWriteTo, true);
            BufferedWriter out = new BufferedWriter(fileWriter);
            out.write(logEntry.serialize());
            out.write("\n");
            out.flush();
            out.close();
        } catch (IOException exception) {
            Log.e(TAG, "could not write to file at " + fileToWriteTo.getAbsolutePath(), exception);
        }
    }

    @Override
    public Flowable<LogEntry> load() {
        if (replayProcessor == null) {
            replayProcessor = ReplayProcessor.create();

            Observable.fromCallable(new Callable<List<LogEntry>>() {
                @Override
                public List<LogEntry> call() throws Exception {
                    return loadLogsFromFile();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Function<List<LogEntry>, ObservableSource<LogEntry>>() {
                        @Override
                        public ObservableSource<LogEntry> apply(List<LogEntry> logEntries) throws Exception {
                            return Observable.fromIterable(logEntries);
                        }
                    })
                    .subscribe(new Consumer<LogEntry>() {
                        @Override
                        public void accept(LogEntry logEntry) throws Exception {
                            replayProcessor.onNext(logEntry);
                        }
                    });
        }

        return replayProcessor;
    }

    @Override
    public File copyToSDCard() {
        final String localFilePath = storageConfig.getPathToFile();
        final String fileName = localFilePath.substring(localFilePath.lastIndexOf("/") + 1);
        final File externalFile = new File(Environment.getExternalStorageDirectory(), fileName);

        try {
            externalFile.mkdirs();
            if (externalFile.exists()) {
                if (externalFile.delete()) {
                    externalFile.createNewFile();
                }
            } else {
                externalFile.createNewFile();
            }

            final List<LogEntry> logEntries = loadLogsFromFile();
            for (LogEntry logEntry : logEntries) {
                write(externalFile, logEntry);
            }
            return externalFile;
        } catch (IOException exception) {
            Log.e(TAG, "could not write to file at " + externalFile.getAbsolutePath(), exception);
        }
        return null;
    }

    @Override
    public void clear() {
        try {
            if (replayProcessor != null) {
                replayProcessor.onComplete();
                replayProcessor = null;
            }

            if (file.delete()) {
                file.createNewFile();
            } else {
                // for some reason we cannot delete the file (access rights)
                // so we just write "nothing" to the file, same end result
                FileWriter fileWriter = new FileWriter(file, false);
                fileWriter.close();
            }
        } catch (IOException exception) {
            Log.e(TAG, "could not write to file at " + file.getAbsolutePath(), exception);
        }
    }

    private List<LogEntry> loadLogsFromFile() {
        List<LogEntry> logs = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader in = new BufferedReader(fileReader);

            String line;
            while ((line = in.readLine()) != null) {
                LogEntry logEntry = LogEntry.deserialize(line);
                logs.add(logEntry);
            }

            in.close();
        } catch (IOException exception) {
            Log.e(TAG, "could not write to file at " + file.getAbsolutePath(), exception);
            return new ArrayList<>();
        }

        return logs;
    }
}
