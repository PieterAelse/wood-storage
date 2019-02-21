package com.jordylangen.woodstorage.storage;

public class StorageConfig {

    private int maxLogCount;
    private int deleteCount;
    private String pathToFile;

    public StorageConfig(int maxLogCount, int deleteCount, String pathToFile) {
        this.maxLogCount = maxLogCount;
        this.deleteCount = deleteCount;
        this.pathToFile = pathToFile;
    }

    public int getMaxLogCount() {
        return maxLogCount;
    }

    public int getDeleteCount() {
        return deleteCount;
    }

    public String getPathToFile() {
        return pathToFile;
    }
}
