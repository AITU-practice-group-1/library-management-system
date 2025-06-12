package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.reservations.*;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import com.example.librarymanagementsystem.Services.ReservationsServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/reservations")
public class ReservationsController {

    private final ReservationsServices reservationsServices;
    private final UserRepository userRepository;

    public ReservationsController(ReservationsServices reservationsServices, UserRepository userRepository) {
        this.reservationsServices = reservationsServices;
        this.userRepository = userRepository;
    }

    @PostMapping
    public String create(@RequestParam UUID userId, @RequestParam UUID bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        ReservationsRequestDTO dto = new ReservationsRequestDTO();
        dto.setBookId(bookId);
        reservationsServices.create(dto, user);
        return "redirect:/reservations?userId=" + userId;
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

    @PutMapping("/{id}/cancel")
    @ResponseBody
    public String cancel(@PathVariable UUID id) {
        reservationsServices.cancel(id);
        return "Reservation cancelled.";
    }

    @PutMapping("/{id}/fulfill")
    @ResponseBody
    public String fulfill(@PathVariable UUID id, @RequestParam UUID byUserId) {
        User byUser = userRepository.findById(byUserId).orElseThrow(() -> new RuntimeException("User not found"));
        reservationsServices.fulfill(id, byUser);
        return "Reservation fulfilled.";
    }
}