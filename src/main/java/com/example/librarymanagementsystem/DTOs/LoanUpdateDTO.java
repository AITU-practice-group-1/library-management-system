package com.example.librarymanagementsystem.DTOs;

import com.example.librarymanagementsystem.Entities.Loan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoanUpdateDTO {
    private LocalDateTime dueDate;

    @NotNull(message = "Loan status must be provided.")
    private Loan.LoanStatus status;
}