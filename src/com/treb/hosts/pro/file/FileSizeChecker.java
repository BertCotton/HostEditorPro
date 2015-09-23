package com.treb.hosts.pro.file;

import java.io.File;

/**
 * Created by Bert on 6/1/13.
 */
public class FileSizeChecker {

    public static long getFileSize() {
        File file = new File("/system/etc/hosts");
        long fileSize = file.length();
        file = null;
        return fileSize;

    }
}
