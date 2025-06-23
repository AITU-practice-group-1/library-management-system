package com.example.librarymanagementsystem.exceptions;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String s) {
        super(s);
    }
}