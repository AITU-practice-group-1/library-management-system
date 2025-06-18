package com.example.librarymanagementsystem.DTOs.book;

import lombok.Data;

import java.util.UUID;

@Data
public class TopRatedBookDTO {
    private UUID id;
    private String title;
    private double ratingAverage;
}

