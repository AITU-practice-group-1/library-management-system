package com.example.librarymanagementsystem.DTOs.loan;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LoanRequestDTO {
    @NotNull(message = "User ID cannot be null.")
    private UUID userId;

    @NotNull(message = "Book ID cannot be null.")
    private UUID bookId;

    @NotNull(message = "Due date cannot be null.")
    @Future(message = "Due date must be in the future.")
    private LocalDateTime dueDate;
}