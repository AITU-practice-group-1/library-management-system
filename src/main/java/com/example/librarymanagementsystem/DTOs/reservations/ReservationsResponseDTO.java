package com.example.librarymanagementsystem.DTOs.reservations;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ReservationsResponseDTO {
    private UUID id;
    private UUID userId;
    private String userEmail;
    private String bookTitle;
    private String status;
    private String reservedAt;
    private String expiresAt;
    private String cancelledAt; // Can be null

    // A builder-style constructor or a mapper is generally preferred, but a constructor is fine too.
    public ReservationsResponseDTO(UUID id, UUID userId, String userEmail, String bookTitle, String status, LocalDateTime reservedAt, LocalDateTime expiresAt, LocalDateTime cancelledAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.bookTitle = bookTitle;
        this.status = status;
        this.reservedAt = reservedAt.format(formatter);
        this.expiresAt = expiresAt.format(formatter);
        if (cancelledAt != null) {
            this.cancelledAt = cancelledAt.format(formatter);
        }
    }
}