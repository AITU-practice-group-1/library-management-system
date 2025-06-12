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

    @ManyToOne
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name ="book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    @CreationTimestamp
    @Column(nullable = false, name = "reserved_at")
    private LocalDateTime reservedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public enum ReservationStatus {
        PENDING,
        FULFILLED,
        CANCELLED
    }
//    public String Email;
//
////    public String Email = user.getEmail();
}