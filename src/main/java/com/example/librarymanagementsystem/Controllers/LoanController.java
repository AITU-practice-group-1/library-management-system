package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.loan.LoanDTO;
import com.example.librarymanagementsystem.DTOs.loan.LoanRequestDTO;
import com.example.librarymanagementsystem.DTOs.loan.LoanResponseDTO;
import com.example.librarymanagementsystem.DTOs.loan.LoanUpdateDTO;
import com.example.librarymanagementsystem.Entities.Loan;
import com.example.librarymanagementsystem.Entities.Reservations;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.Repositories.ReservationsRepository;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import com.example.librarymanagementsystem.Services.BookService;
import com.example.librarymanagementsystem.Services.LoanServices;
import com.example.librarymanagementsystem.Services.NotificationSender;
import com.example.librarymanagementsystem.Services.UserServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/loans")
public class LoanController {

    private final LoanServices loanServices;
    private final UserServices userServices;
    private final BookRepository bookRepository;
    private final NotificationSender notificationSender;
    private final ReservationsRepository reservationsRepository;
    private final UserRepository userRepository;

  

//    @GetMapping
//    public String listAllLoans(Model model) {
//        // This endpoint should be for librarians/admins to see all loans
//        List<LoanResponseDTO> loans = loanServices.findAll();
//        model.addAttribute("loans", loans);
//        model.addAttribute("canEdit", true); // Assuming this view is for authorized staff
//        return "loan/list";
//    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String showAllLoans(@RequestParam(value = "keyword", required = false) String keyword,
                               @PageableDefault(size = 10, sort = "issueDate", direction = Sort.Direction.DESC) Pageable pageable,
                               Model model) {
        // Call the new service method to get paginated and filtered results
        Page<LoanResponseDTO> loanPage = loanServices.findAllWithSearch(keyword, pageable);

        model.addAttribute("loans", loanPage.getContent()); // The list of loans for the current page
        model.addAttribute("loanPage", loanPage); // The full page object for pagination controls
        model.addAttribute("keyword", keyword); // Pass the keyword back to the view to keep it in the search bar

        return "all-loans"; // The name of your new Thymeleaf template
    }
  
    @PostMapping("{id}/notify")
    public String notifyUser(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        LoanResponseDTO loan = loanServices.findById(id);
        notificationSender.notifyDueDate(loan.getUserEmail(),loan.getBookTitle(), loan.getBookAuthor(), loan.getDueDate());
        redirectAttributes.addFlashAttribute("notifiedId", loan.getId());
        return "redirect:/loans";
    }

    @GetMapping("/my-loans")
    public String listMyLoans(Model model) throws Exception {
        var userDto = userServices.getAuthenticatedUser();
        List<LoanResponseDTO> loans = loanServices.getLoansByUserId(userDto.getId());
        model.addAttribute("loans", loans);
        model.addAttribute("canEdit", false); // Users cannot edit their own loans
        return "loan/list";
    }

    @GetMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String returnLoanForm(@PathVariable UUID id, Model model) {
        LoanResponseDTO loan = loanServices.findById(id);
        LoanUpdateDTO updateDTO = new LoanUpdateDTO();
        updateDTO.setStatus(Loan.LoanStatus.RETURNED); // Pre-populate the status for return

        model.addAttribute("loan", loan);
        model.addAttribute("updateDTO", updateDTO);
        return "loan/return"; // A specific view for returning a book
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String processReturn(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            // The service logic already handles incrementing the book's available copies
            LoanUpdateDTO updateDTO = new LoanUpdateDTO();
            updateDTO.setStatus(Loan.LoanStatus.RETURNED);
            loanServices.updateLoan(id, updateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing return: " + e.getMessage());
        }
        return "redirect:/loans"; // Redirect back to the main loan list
    }
    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String createLoanForm(Model model) {
        model.addAttribute("loanRequest", new LoanRequestDTO());
        model.addAttribute("availableBooks", bookRepository.findAll());
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
    public String createLoan(@Valid @ModelAttribute("loanRequest") LoanRequestDTO loanRequest,
                             BindingResult result,
                             Principal principal,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("availableBooks", bookRepository.findAll());
            return "loan/create";
        }

        var librarian = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Librarian not found"));

        loanServices.createLoan(loanRequest, librarian);
        return "redirect:/loans";
    }



    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String editLoanForm(@PathVariable UUID id, Model model) {
        var loanDTO = loanServices.findById(id);
        LoanUpdateDTO updateDTO = new LoanUpdateDTO();
        updateDTO.setDueDate(loanDTO.getDueDate());

        model.addAttribute("loan", loanDTO);
        model.addAttribute("updateDTO", updateDTO);
        return "loan/edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String updateLoan(@PathVariable UUID id, @Valid @ModelAttribute("updateDTO") LoanUpdateDTO updateDTO, RedirectAttributes redirectAttributes) {
        try {
            loanServices.updateLoan(id, updateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Loan updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update loan: " + e.getMessage());
        }
        return "redirect:/loans";
    }
}
