package com.example.librarymanagementsystem.DTOs.feedback;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FeedbackResponseDTO {
    private UUID id;
    private UUID userId;
    private String firstName;
    private UUID bookId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
