package com.example.librarymanagementsystem.DTOs.feedback;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FeedbackUpdateDTO {
    private UUID id;
    private Integer rating;
    private String comment;
}
