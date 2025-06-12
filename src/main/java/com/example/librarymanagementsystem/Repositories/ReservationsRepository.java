package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.Reservations;
import com.example.librarymanagementsystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservationsRepository extends JpaRepository<Reservations, UUID> {
    List<Reservations> findByUser(User user);
    List<Reservations> findByUserEmailAndStatus(String email, Reservations.ReservationStatus status);
}