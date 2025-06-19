package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.LoanDTO;
import com.example.librarymanagementsystem.DTOs.LoanUpdateDTO;
import com.example.librarymanagementsystem.Entities.Loan;
import com.example.librarymanagementsystem.Entities.Reservations;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.Repositories.LoanRepository;
import com.example.librarymanagementsystem.Repositories.ReservationsRepository;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import com.example.librarymanagementsystem.Services.LoanServices;
import com.example.librarymanagementsystem.Services.ReservationsServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/loans")
public class LoanController {

    private final LoanServices loanServices;
    private final UserRepository userRepository;
    private final ReservationsRepository reservationsRepository;
    private final ReservationsServices  reservationsServices;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    @GetMapping
    public String listLoans(Model model, Principal principal) {
        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<LoanDTO> loans;
        boolean canEdit = user.getRole().equals("ADMIN") || user.getRole().equals("LIBRARIAN");

        if (canEdit) {
            loans = loanServices.findAll();
        } else {
            loans = loanServices.getLoansByUserId(user.getId());
        }

        model.addAttribute("loans", loans);
        model.addAttribute("canEdit", canEdit);
        return "loan/list";
    }


    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String searchApprovedForm() {
        return "loan/search"; // форма ввода email
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String searchApprovedReservations(@RequestParam String email, Model model) {
        List<Reservations> reservations = reservationsRepository
                .findByUserEmailAndStatus(email, Reservations.ReservationStatus.FULFILLED);

        model.addAttribute("reservations", reservations);
        model.addAttribute("email", email);
        return "loan/reservation-list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String createLoanForm(Model model) {
        model.addAttribute("loan", new LoanDTO());
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "loan/create";
    }

    @GetMapping("/create-from-reservation/{reservationId}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String createLoanFromReservation(@PathVariable UUID reservationId, Model model) {
        Reservations res = reservationsRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setUserId(res.getUser().getId());
        loanDTO.setBookId(res.getBook().getId());
        loanDTO.setIssueDate(LocalDateTime.now());

        model.addAttribute("loan", loanDTO);
        model.addAttribute("userEmail", res.getUser().getEmail());
        model.addAttribute("bookTitle", res.getBook().getTitle());
        model.addAttribute("predefined", true); // флаг, чтобы в шаблоне понять откуда вызов

        return "loan/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String createLoan(@Valid @ModelAttribute("loan") LoanDTO loanDTO, Principal principal) {
        var librarian = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Librarian not found"));

        loanDTO.setIssuedBy(librarian.getId());
        loanServices.createLoan(loanDTO);

        if (loanDTO.getReservationId() != null) {
            reservationsServices.fulfill(loanDTO.getReservationId(), librarian);
        }

        return "redirect:/loans";
    }




    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String findLoanById(@PathVariable UUID id, Model model) {
        model.addAttribute("loan", loanServices.findById(id));
        return "loan/detail";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String editLoanForm(@PathVariable UUID id, Model model) {
        var loanDTO = loanServices.findById(id); // LoanDTO
        LoanUpdateDTO updateDTO = new LoanUpdateDTO();
        updateDTO.setDueDate(loanDTO.getDueDate());
        updateDTO.setStatus(Loan.LoanStatus.valueOf(loanDTO.getStatus()));

        model.addAttribute("loan", loanDTO); // для отображения инфо
        model.addAttribute("updateDTO", updateDTO); // для формы редактирования
        return "loan/edit";
    }


    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String updateLoan(@PathVariable UUID id, @ModelAttribute("updateDTO") LoanUpdateDTO updateDTO) {
        loanServices.updateLoan(id, updateDTO);
        return "redirect:/loans";
    }






}
