package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.loan.LoanResponseDTO;
import com.example.librarymanagementsystem.DTOs.loan.LoanUpdateDTO;
import com.example.librarymanagementsystem.Entities.Loan;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/loans")
public class LoanController {

    private final LoanServices loanServices;
    private final UserServices userServices;
    private final NotificationSender notificationSender;
  

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
