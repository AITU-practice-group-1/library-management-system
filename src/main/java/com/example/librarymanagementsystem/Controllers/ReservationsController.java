package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.DTOs.reservations.*;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import com.example.librarymanagementsystem.Services.BookService;
import com.example.librarymanagementsystem.Services.ReservationsServices;
import com.example.librarymanagementsystem.Services.UserServices;
import com.example.librarymanagementsystem.exceptions.BookNotAvailableException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/reservations")
public class ReservationsController {

    private final ReservationsServices reservationsServices;
    private final UserRepository userRepository;
    private final UserServices userServices;
    private final BookService bookService;

    public ReservationsController(ReservationsServices reservationsServices, UserRepository userRepository, UserServices userServices, BookService bookService) {
        this.reservationsServices = reservationsServices;
        this.userRepository = userRepository;
        this.userServices = userServices;
        this.bookService = bookService;
    }

    @PostMapping("/create")
    public String create(@RequestParam UUID bookId, RedirectAttributes redirectAttributes) throws BookNotAvailableException {
        try {
            UserDTO authenticatedUser = userServices.getAuthenticatedUser();
            User user = userServices.getUserById(authenticatedUser.getId());

            ReservationsRequestDTO dto = new ReservationsRequestDTO();
            dto.setBookId(bookId);

            reservationsServices.create(dto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Book reserved successfully!");
            return "redirect:/books/" + bookId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error reserving book: " + e.getMessage());
            return "redirect:/books/" + bookId;
        }
    }


    @GetMapping
    public String listByUser(@RequestParam UUID userId, Model model) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<ReservationsResponseDTO> reservations = reservationsServices.listByUser(user);
        model.addAttribute("reservations", reservations);
        model.addAttribute("userId", userId);
        return "reservations";
    }

    @GetMapping("/all")
    public String listAll(Model model) {
        List<ReservationsResponseDTO> reservations = reservationsServices.listAll();
        model.addAttribute("reservations", reservations);
        return "all-reservations";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            reservationsServices.cancel(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel reservation: " + e.getMessage());
        }
        // Redirect back to the page the user was on, e.g., their personal reservation list
        return "redirect:/books";
    }

    @PostMapping("/{id}/fulfill")
    public String fulfill(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            UserDTO authenticatedLibrarian = userServices.getAuthenticatedUser();
            User librarian = userServices.getUserById(authenticatedLibrarian.getId());

            reservationsServices.fulfill(id, librarian);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation fulfilled and loan created.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to fulfill reservation: " + e.getMessage());
        }
        return "redirect:/reservations/all";
    }

}