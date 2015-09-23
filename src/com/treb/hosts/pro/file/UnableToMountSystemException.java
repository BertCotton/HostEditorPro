package com.treb.hosts.pro.file;

public class UnableToMountSystemException extends Exception {
    public UnableToMountSystemException(Throwable ex) {
        super(ex);
    }

    public UnableToMountSystemException(String message) {
        super(message);
    }

    public UnableToMountSystemException(String message, Throwable ex) {
        super(message, ex);
    }
}
