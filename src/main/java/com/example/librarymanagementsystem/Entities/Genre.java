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
    POETRY("Poetry"),
    COMEDY("Comedy"),
    FICTION("Fiction"),
    DYSTOPIAN_FICTION("Dystopian Fiction"),
    ADVENTURE("Adventure"),
    EPIC_POETRY("Epic Poetry"),
    PHILOSOPHICAL_FICTION("Philosophical Fiction"),
    SATIRE("Satire"),
    GOTHIC_FICTION("Gothic Fiction"),
    ABSURDIST_FICTION("Absurdist Fiction"),
    MAGICAL_REALISM("Magical Realism"),
    MODERNIST_FICTION("Modernist Fiction");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }
}