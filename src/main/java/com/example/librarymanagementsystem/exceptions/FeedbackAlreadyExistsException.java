package com.example.librarymanagementsystem.exceptions;

public class FeedbackAlreadyExistsException extends RuntimeException {
    public FeedbackAlreadyExistsException(String message) {
        super(message);
    }

    public FeedbackAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
