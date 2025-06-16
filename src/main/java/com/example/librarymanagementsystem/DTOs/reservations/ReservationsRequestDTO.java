package com.example.librarymanagementsystem.DTOs.reservations;

import lombok.Data;
import java.util.UUID;

@Data
public class ReservationsRequestDTO {
    private UUID bookId;
}