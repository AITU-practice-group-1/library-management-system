package com.example.librarymanagementsystem.DTOs.book;

import com.example.librarymanagementsystem.util.Genre;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class BookResponseDTO {
    private UUID id;
    private String title;
    private String author;
    private String publisher;
    private Integer publishedYear;
    private String isbn;
    private Genre genre;
    private String description;
    private Integer totalCopies;
    private int availableCopies;
    private long ratingCount;
    private BigDecimal ratingAverage;
}