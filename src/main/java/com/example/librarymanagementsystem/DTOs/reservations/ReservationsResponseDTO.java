package com.example.librarymanagementsystem.DTOs.reservations;

import lombok.Data;
import java.util.UUID;

@Data
public class ReservationsResponseDTO {
    private UUID id;
    private UUID userId;
    private String bookTitle;
    private String status;
    private String reservedAt;
    private String email;

    public ReservationsResponseDTO() {

    }

    public ReservationsResponseDTO(UUID id, UUID userId, String email, String bookTitle, String status, String reservedAt) {
        this.id = id;
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.status = status;
        this.reservedAt = reservedAt;
        this.email = email;
    }
}