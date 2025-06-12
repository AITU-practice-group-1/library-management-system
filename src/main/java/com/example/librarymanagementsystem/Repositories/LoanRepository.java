package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByUserId(UUID userId);
}
