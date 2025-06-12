package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.reservations.ReservationsRequestDTO;
import com.example.librarymanagementsystem.DTOs.reservations.ReservationsResponseDTO;
import com.example.librarymanagementsystem.Entities.*;
import com.example.librarymanagementsystem.Repositories.ReservationsRepository;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationsServices {

    private final ReservationsRepository reservationsRepository;
    private final BookRepository bookRepository;

    public ReservationsServices(ReservationsRepository reservationsRepository, BookRepository bookRepository) {
        this.reservationsRepository = reservationsRepository;
        this.bookRepository = bookRepository;
    }

    public void create(ReservationsRequestDTO dto, User user) {
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Reservations reservation = new Reservations();
        reservation.setBook(book);
        reservation.setUser(user);
        reservation.setStatus(Reservations.ReservationStatus.PENDING);
        reservation.setExpiresAt(LocalDateTime.now().plusDays(3));

        reservationsRepository.save(reservation);
    }

    public List<ReservationsResponseDTO> listByUser(User user) {
        return reservationsRepository.findByUser(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReservationsResponseDTO> listAll() {
        return reservationsRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancel(UUID id) {
        Reservations reservation = reservationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus(Reservations.ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
    }

    @Transactional
    public void fulfill(UUID id, User byUser) {
        Reservations reservation = reservationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus(Reservations.ReservationStatus.FULFILLED);
    }

    private ReservationsResponseDTO toDto(Reservations reservation) {
        ReservationsResponseDTO dto = new ReservationsResponseDTO();
        dto.setId(reservation.getId());
        dto.setBookTitle(reservation.getBook().getTitle());
        dto.setStatus(reservation.getStatus().name());
        dto.setReservedAt(reservation.getReservedAt().toString());
        return dto;
    }

    public List<ReservationsResponseDTO> findApprovedByEmail(String email) {
        return reservationsRepository
                .findByUserEmailAndStatus(email, Reservations.ReservationStatus.FULFILLED)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}