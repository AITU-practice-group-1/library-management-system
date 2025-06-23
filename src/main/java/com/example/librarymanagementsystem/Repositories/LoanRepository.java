package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.DTOs.book.BookResponseDTO;
import com.example.librarymanagementsystem.DTOs.book.ContractBookDTO;
import com.example.librarymanagementsystem.Entities.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByUserId(UUID userId);

    Page<Loan> findAll(Specification<Loan> spec, Pageable pageable);

    @Query("select new com.example.librarymanagementsystem.DTOs.Users.UserDTO(l.user.id, l.user.email, l.user.firstName, l.user.lastName) from Loan  l where l.id = :id")
    Optional<UserDTO> findUserDtoByLoan(UUID id);

    @Query("select new com.example.librarymanagementsystem.DTOs.book.ContractBookDTO(l.book.title, l.book.author, l.book.isbn) from Loan  l where l.id = :id")
    Optional<ContractBookDTO> findBookDtoByLoan(UUID id);
}
