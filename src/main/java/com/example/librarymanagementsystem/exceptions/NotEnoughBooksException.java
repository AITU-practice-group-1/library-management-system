package com.example.librarymanagementsystem.exceptions;

public class NotEnoughBooksException extends RuntimeException {
    public NotEnoughBooksException(String message) {
        super(message);
    }

    public NotEnoughBooksException(String message, Throwable cause) {
        super(message, cause);
    }
}
