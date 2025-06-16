package com.example.librarymanagementsystem.DTOs;

import com.example.librarymanagementsystem.Entities.Loan;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LoanUpdateDTO{

    private LocalDate dueDate;
    private Loan.LoanStatus status;

}
