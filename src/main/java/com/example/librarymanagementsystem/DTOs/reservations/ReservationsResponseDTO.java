package com.example.librarymanagementsystem.DTOs.reservations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime; // Import this
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ReservationsResponseDTO {
    private UUID id;
    private UUID userId;
    private String userEmail;
    private String bookTitle;
    private String status;
    private LocalDateTime reservedAt; // Change from String to LocalDateTime
    private LocalDateTime expiresAt;  // Change from String to LocalDateTime
    private LocalDateTime cancelledAt; // Change from String to LocalDateTime
}