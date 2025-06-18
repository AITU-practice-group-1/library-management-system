package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.DTOs.Users.UserStatisticsBookDTO;
import com.example.librarymanagementsystem.Entities.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserStatisticsBookRepository extends JpaRepository<Loan, UUID> {
    @Query("select new com.example.librarymanagementsystem.DTOs.Users.UserStatisticsBookDTO(" +
            "r.book.title, r.book.id, CAST(r.status AS string)) from Reservations r where r.status = 'PENDING' and r.user.id = :id" )
    Page<UserStatisticsBookDTO> findAllUserReservedBooks(UUID id, Pageable pageable);

    @Query("select new com.example.librarymanagementsystem.DTOs.Users.UserStatisticsBookDTO(" +
            "r.book.title, r.book.id, CAST(r.status AS string)) from Loan r where r.status = 'BORROWED' and r.user.id = :id")
    Page<UserStatisticsBookDTO> findAllUserLoanedBooks(UUID id, Pageable pageable);

    @Query("select new com.example.librarymanagementsystem.DTOs.Users.UserStatisticsBookDTO(" +
            "r.book.title, r.book.id, CAST(r.status AS string)) from Loan r where r.status = 'RETURNED' and r.user.id = :id")
    Page<UserStatisticsBookDTO> findAllUserReadBooks(UUID id, Pageable pageable);
}
