package com.example.librarymanagementsystem.DTOs.book;

import com.example.librarymanagementsystem.util.Genre;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDTO {
    private UUID id;

    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @NotEmpty(message = "Author cannot be empty")
    private String author;

    @NotEmpty(message = "Publisher cannot be empty")
    private String publisher;

    @NotNull(message = "Published year cannot be null")
    @Max(value = 2025, message = "Published year cannot be in the future")
    @Min(value = 1000, message = "Published year seems too old")
    private Integer publishedYear;

    @NotEmpty(message = "ISBN cannot be empty")
    @Pattern(regexp = "^\\d{13}$", message = "Invalid ISBN format")
    private String isbn;

    @NotNull(message = "Genre must be selected")
    private Genre genre;

    @NotEmpty(message = "Description cannot be empty")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Total copies cannot be null")
    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;

    @NotNull(message = "Available copies must be provided")
    @Min(value = 0, message = "Available copies cannot be negative")
    private int availableCopies;

    private long ratingSum = 0L;

    private long ratingCount = 0L;

    private BigDecimal ratingAverage = BigDecimal.ZERO;
}