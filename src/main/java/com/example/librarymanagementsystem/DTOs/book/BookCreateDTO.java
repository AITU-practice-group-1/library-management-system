package com.example.librarymanagementsystem.DTOs.book;

import com.example.librarymanagementsystem.util.Genre;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookCreateDTO {
    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Author cannot be empty")
    private String author;

    @NotBlank(message = "Publisher cannot be empty")
    private String publisher;

    @NotNull(message = "Published year cannot be null")
    @Min(value = 1000, message = "Published year seems too old")
    private Integer publishedYear;

    @NotBlank(message = "ISBN cannot be empty")
    @Pattern(regexp = "^\\d{13}$", message = "ISBN must be a 13-digit number")
    private String isbn;

    @NotNull(message = "Genre must be selected")
    private Genre genre;

    @NotBlank(message = "Description cannot be empty")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Total copies cannot be null")
    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;
}