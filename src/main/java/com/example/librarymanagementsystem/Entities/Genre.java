package com.example.librarymanagementsystem.Entities;

import lombok.Getter;

@Getter
public enum Genre {
    FANTASY("Fantasy"),
    SCIENCE_FICTION("Science Fiction"),
    MYSTERY("Mystery"),
    THRILLER("Thriller"),
    ROMANCE("Romance"),
    HORROR("Horror"),
    HISTORICAL_FICTION("Historical Fiction"),
    NON_FICTION("Non-Fiction"),
    BIOGRAPHY("Biography"),
    POETRY("Poetry");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }

}