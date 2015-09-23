package com.treb.hosts.pro.backup;

import java.util.Date;

public class BackupFile {
    private String fileName;
    private String path;
    private Date savedDate;

    public BackupFile(String fileName, String path, Date savedDate) {
        this.fileName = fileName;
        this.path = path;
        this.savedDate = savedDate;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public Date getSavedDate() {
        return savedDate;
    }
}
