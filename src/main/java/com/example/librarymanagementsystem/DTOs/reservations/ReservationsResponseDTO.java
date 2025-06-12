package com.example.librarymanagementsystem.DTOs.reservations;

import lombok.Data;
import java.util.UUID;

@Data
public class ReservationsResponseDTO {
    private UUID id;
    private String bookTitle;
    private String status;
    private String reservedAt;
}