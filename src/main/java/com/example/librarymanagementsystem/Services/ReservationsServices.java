package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.LoanRequestDTO;
import com.example.librarymanagementsystem.DTOs.LoanResponseDTO;
import com.example.librarymanagementsystem.DTOs.reservations.ReservationsRequestDTO;
import com.example.librarymanagementsystem.DTOs.reservations.ReservationsResponseDTO;
import com.example.librarymanagementsystem.Entities.*;
import com.example.librarymanagementsystem.Repositories.ReservationsRepository;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.exceptions.BookNotAvailableException;
import com.example.librarymanagementsystem.exceptions.BookNotFoundException;
import com.example.librarymanagementsystem.exceptions.ReservationNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationsServices {

    private static final Logger logger = LoggerFactory.getLogger(ReservationsServices.class);

    private final ReservationsRepository reservationsRepository;
    private final BookRepository bookRepository;
    private final LoanServices loanService; // Inject LoanService to create a loan

    public ReservationsServices(ReservationsRepository reservationsRepository, BookRepository bookRepository, LoanServices loanService) {
        this.reservationsRepository = reservationsRepository;
        this.bookRepository = bookRepository;
        this.loanService = loanService;
    }


    @Transactional
    public Reservations create(ReservationsRequestDTO dto, User user) throws BookNotAvailableException {
        logger.info("Creating reservation for user: {} and bookId: {}", user.getEmail(), dto.getBookId());
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + dto.getBookId()));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No copies of the book are available for reservation.");
        }

        // Decrement available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Reservations reservation = new Reservations();
        reservation.setBook(book);
        reservation.setUser(user);
        reservation.setStatus(Reservations.ReservationStatus.PENDING);
        // Set an expiration time, e.g., 24 hours from now
        reservation.setExpiresAt(LocalDateTime.now().plusHours(24));

        Reservations savedReservation = reservationsRepository.save(reservation);
        logger.info("Reservation created with id: {}", savedReservation.getId());
        return savedReservation;
    }


    public List<ReservationsResponseDTO> listByUser(User user) {
        logger.info("Fetching reservations for user: {}", user.getEmail());
        return reservationsRepository.findByUser(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional // Use readOnly for performance
    public List<ReservationsResponseDTO> listAll() {
        List<Reservations> reservations = reservationsRepository.findAll(); // Assuming you have a repository

        // Convert each Entity to a DTO *inside the transaction*
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional // Use readOnly for performance
    public List<ReservationsResponseDTO> listAllPending() {
        List<Reservations> reservations = reservationsRepository.findAllByStatus(Reservations.ReservationStatus.PENDING); // Assuming you have a repository

        // Convert each Entity to a DTO *inside the transaction*
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public void expireReservations() {
        logger.info("Running scheduled task to expire reservations.");
        List<Reservations> expiredList = reservationsRepository.findByStatusAndExpiresAtBefore(
                Reservations.ReservationStatus.PENDING, LocalDateTime.now()
        );

        if (expiredList.isEmpty()) {
            logger.info("No expired reservations found.");
            return;
        }

        for (Reservations reservation : expiredList) {
            reservation.setStatus(Reservations.ReservationStatus.EXPIRED);

            // Return the book copy to the available pool
            Book book = reservation.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);

            logger.info("Reservation {} has expired. Book '{}' returned to stock.", reservation.getId(), book.getTitle());
        }
        reservationsRepository.saveAll(expiredList);
    }

    public List<ReservationsResponseDTO> listAllWthEmail() {
        logger.info("Fetching all reservations");
        return reservationsRepository.findAllWithDetails();
    }

    @Transactional
    public void cancel(UUID id) {
        logger.info("Cancelling reservation with id: {}", id);
        Reservations reservation = reservationsRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + id));

        // Only PENDING reservations can be cancelled
        if (reservation.getStatus() == Reservations.ReservationStatus.PENDING) {
            reservation.setStatus(Reservations.ReservationStatus.CANCELLED);
            reservation.setCancelledAt(LocalDateTime.now());

            // Return the book copy to the available pool
            Book book = reservation.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
            logger.info("Book copy for '{}' returned to stock.", book.getTitle());
        } else {
            logger.warn("Attempted to cancel a reservation that is not in PENDING state. Status: {}", reservation.getStatus());
        }
    }


    @Transactional
    public void fulfill(UUID reservationId, User librarian) {
        logger.info("Fulfilling reservation with id: {} by librarian: {}", reservationId, librarian.getEmail());
        Reservations reservation = reservationsRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + reservationId));

        if (reservation.getStatus() != Reservations.ReservationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING reservations can be fulfilled.");
        }

        reservation.setStatus(Reservations.ReservationStatus.FULFILLED);

        // Create a new loan from the fulfilled reservation
        LoanRequestDTO loanDto = new LoanRequestDTO();
        loanDto.setUserId(reservation.getUser().getId());
        loanDto.setBookId(reservation.getBook().getId());
        // Standard loan period, e.g., 14 days
        loanDto.setDueDate(LocalDateTime.now().plusDays(14));

        LoanResponseDTO loan = loanService.createLoan(loanDto, librarian);

        logger.info("Reservation with id {} marked as FULFILLED and Loan created.", reservationId);
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

    private ReservationsResponseDTO convertToDto(Reservations reservation) {
        ReservationsResponseDTO dto = new ReservationsResponseDTO();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUser().getId());
        dto.setUserEmail(reservation.getUser().getEmail());
        dto.setBookTitle(reservation.getBook().getTitle());
        dto.setStatus(reservation.getStatus().name());
        dto.setReservedAt(reservation.getReservedAt());
        dto.setExpiresAt(reservation.getExpiresAt());

        if (reservation.getCancelledAt() != null) {
            dto.setCancelledAt(reservation.getCancelledAt());
        }

        return dto;
    }

    private ReservationsResponseDTO toDto(Reservations reservation) {
        ReservationsResponseDTO dto = new ReservationsResponseDTO();
        dto.setId(reservation.getId());
        dto.setBookTitle(reservation.getBook().getTitle());
        dto.setStatus(reservation.getStatus().name());
        dto.setReservedAt(reservation.getReservedAt()); // Update this as well
        return dto;
    }


}