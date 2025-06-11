package com.example.librarymanagementsystem.DTOs;

import com.example.librarymanagementsystem.Entities.Loan;
import lombok.Data;

import java.math.BigDecimal;
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
    private LocalDateTime dueDate;
    private String status;
}

