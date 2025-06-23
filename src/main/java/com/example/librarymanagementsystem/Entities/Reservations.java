package com.example.librarymanagementsystem.Entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Data
public class Reservations {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    @CreationTimestamp
    @Column(nullable = false, name = "reserved_at", updatable = false)
    private LocalDateTime reservedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public enum ReservationStatus {
        PENDING,    // User has reserved, waiting for pickup
        FULFILLED,  // User has picked up the book and a loan was created
        CANCELLED,  // User manually cancelled the reservation
        EXPIRED     // Reservation expired due to timeout
    }
}