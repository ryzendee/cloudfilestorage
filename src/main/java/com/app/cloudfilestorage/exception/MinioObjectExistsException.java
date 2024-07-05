package com.app.cloudfilestorage.exception;

public class MinioObjectExistsException extends RuntimeException {
    public MinioObjectExistsException(String message) {
        super(message);
    }
}
