package com.example.librarymanagementsystem.DTOs.feedback;

import lombok.Data;
import javax.validation.constraints.*;
import java.util.UUID;

@Data
public class FeedbackRequestDTO {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Book ID is required")
    private UUID bookId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    private String comment;
}