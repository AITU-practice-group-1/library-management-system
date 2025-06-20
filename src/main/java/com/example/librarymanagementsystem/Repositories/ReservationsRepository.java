package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.DTOs.reservations.ReservationsResponseDTO;
import com.example.librarymanagementsystem.Entities.Reservations;
import com.example.librarymanagementsystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ReservationsRepository extends JpaRepository<Reservations, UUID> {
    List<Reservations> findByUser(User user);
    List<Reservations> findByUserEmailAndStatus(String email, Reservations.ReservationStatus status);

    // This method is crucial for the scheduled task to find expired reservations.
    List<Reservations> findByStatusAndExpiresAtBefore(Reservations.ReservationStatus status, LocalDateTime currentTime);

    @Query("SELECT new com.example.librarymanagementsystem.DTOs.reservations.ReservationsResponseDTO(" +
            "r.id, r.user.id, r.user.email, r.book.title, CAST(r.status AS string), r.reservedAt, r.expiresAt, r.cancelledAt) " +
            "FROM Reservations r")

    List<ReservationsResponseDTO> findAllWithDetails();

//    List<ReservationsResponseDTO> findAllWithEmail();

    @Query("SELECT r.book.id FROM Reservations r WHERE r.user = :user AND r.status = 'PENDING'")
    Set<UUID> findActiveReservationBookIdsByUser(@Param("user") User user);
}