package com.example.librarymanagementsystem.DTOs.loan;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LoanDTO {
    private UUID id;
    private UUID userId;
    private UUID bookId;
    private UUID issuedBy;
    private LocalDateTime issueDate;
    private LocalDateTime returnDate;
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    private String status;
    private LocalDateTime updatedAt;

    private UUID reservationId;

    private String userEmail;
    private String bookTitle;
    private String bookAuthor;
    private String issuedByEmail;
}
