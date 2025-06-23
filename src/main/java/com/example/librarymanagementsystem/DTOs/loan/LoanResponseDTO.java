package com.example.librarymanagementsystem.DTOs.loan;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LoanResponseDTO {
    private UUID id;
    private String bookTitle;
    private String bookIsbn;
    private String userEmail;
    private String issuedByEmail;
    private LocalDateTime issueDate;
    private LocalDateTime returnDate;
    private LocalDateTime dueDate;
    private String status;
    private boolean isOverdue;

    private String bookAuthor;
}