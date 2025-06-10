package com.example.librarymanagementsystem.DTOs.book;

import com.example.librarymanagementsystem.Entities.Genre;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    // Using the current year requires a bit more work, so for simplicity we'll hardcode a value.
    // In a real app, you might use a custom validator.
    @NotNull(message = "Published year cannot be null")
    @Max(value = 2025, message = "Published year cannot be in the future")
    @Min(value = 1000, message = "Published year seems too old")
    private Integer publishedYear; // Use Integer to allow @NotNull check

    @NotEmpty(message = "ISBN cannot be empty")
    // A common ISBN regex for both ISBN-10 and ISBN-13
    @Pattern(regexp = "^(?:ISBN(?:-13)?:?)(?=[0-9]{13}$|[0-9]{9}X$)([0-9-]{10,16})$", message = "Invalid ISBN format")
    private String isbn;

    @NotNull(message = "Genre must be selected") // Use @NotNull for enums/objects
    private Genre genre;

    @NotEmpty(message = "Description cannot be empty")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Total copies cannot be null")
    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies; // Use Integer to allow @NotNull check

    private int availableCopies;
}