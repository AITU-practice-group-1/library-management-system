package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.Loan;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByUserId(UUID userId);

    List<Loan> findAllByDueDateBeforeAndStatus(LocalDateTime date, Loan.LoanStatus status);
    Page<Loan> findAll(Specification<Loan> spec, Pageable pageable);
}
