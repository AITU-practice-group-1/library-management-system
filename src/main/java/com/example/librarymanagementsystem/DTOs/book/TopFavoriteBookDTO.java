package com.example.librarymanagementsystem.DTOs.book;

import lombok.Data;

import java.util.UUID;

@Data
public class TopFavoriteBookDTO {
    private UUID id;
    private String title;
    private int favoriteCount;
}
