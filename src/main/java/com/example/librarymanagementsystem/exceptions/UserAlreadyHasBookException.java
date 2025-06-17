package com.example.librarymanagementsystem.exceptions;

public class UserAlreadyHasBookException extends RuntimeException {
    public UserAlreadyHasBookException(String message) {
        super(message);
    }

    public UserAlreadyHasBookException(String message, Throwable cause) {
        super(message, cause);
    }
}
