package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.reservations.ReservationsRequestDTO;
import com.example.librarymanagementsystem.DTOs.reservations.ReservationsResponseDTO;
import com.example.librarymanagementsystem.Entities.*;
import com.example.librarymanagementsystem.Repositories.ReservationsRepository;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.exceptions.BookNotFoundException;
import com.example.librarymanagementsystem.exceptions.ReservationNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationsServices {

    private static final Logger logger = LoggerFactory.getLogger(ReservationsServices.class);

    private final ReservationsRepository reservationsRepository;
    private final BookRepository bookRepository;
    private final UserServices userServices;

    public void create(ReservationsRequestDTO dto, User user) {
        logger.info("Creating reservation for user: {} and bookId: {}", user.getEmail(), dto.getBookId());
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> {
                    logger.error("Book with id {} not found", dto.getBookId());
                    return new BookNotFoundException("Book not found with ID: " + dto.getBookId());
                });
        if (userServices.isUserBanned(user.getId())) {
            throw new IllegalStateException("User is banned and cannot borrow books.");
        }
        Reservations reservation = new Reservations();
        reservation.setBook(book);
        reservation.setUser(user);
        reservation.setStatus(Reservations.ReservationStatus.PENDING);
        reservation.setExpiresAt(LocalDateTime.now().plusDays(3));

        reservationsRepository.save(reservation);
        logger.info("Reservation created with id: {}", reservation.getId());
    }

    public List<ReservationsResponseDTO> listByUser(User user) {
        logger.info("Fetching reservations for user: {}", user.getEmail());
        return reservationsRepository.findByUser(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReservationsResponseDTO> listAll() {
        logger.info("Fetching all reservations");
        return reservationsRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    public List<ReservationsResponseDTO> listAllWthEmail() {
        logger.info("Fetching all reservations");
        return reservationsRepository.findAllWithEmail();
    }

    @Transactional
    public void cancel(UUID id) {
        logger.info("Cancelling reservation with id: {}", id);
        Reservations reservation = reservationsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Reservation with id {} not found", id);
                    return new ReservationNotFoundException("Reservation not found with ID: " + id);
                });

        reservation.setStatus(Reservations.ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        logger.info("Reservation with id {} has been cancelled", id);
    }

    @Transactional
    public void fulfill(UUID id, User byUser) {
        logger.info("Fulfilling reservation with id: {} by user: {}", id, byUser.getEmail());
        Reservations reservation = reservationsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Reservation with id {} not found", id);
                    return new ReservationNotFoundException("Reservation not found with ID: " + id);
                });

        reservation.setStatus(Reservations.ReservationStatus.FULFILLED);
        logger.info("Reservation with id {} marked as fulfilled", id);
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
        logger.info("Fetching fulfilled reservations for email: {}", email);
        return reservationsRepository
                .findByUserEmailAndStatus(email, Reservations.ReservationStatus.FULFILLED)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Set<UUID> getActiveReservationBookIdsByUser(User user) {
        if (user == null) {
            return Collections.emptySet();
        }
        logger.info("Fetching active reservation book IDs for user: {}", user.getEmail());
        return reservationsRepository.findActiveReservationBookIdsByUser(user);
    }
}