package com.app.cloudfilestorage.exception;

public class MinioRepositoryException extends RuntimeException {

    public MinioRepositoryException(Throwable cause) {
        super(cause);
    }

    public MinioRepositoryException(String message) {
        super(message);
    }

    public MinioRepositoryException() {
    }
}
