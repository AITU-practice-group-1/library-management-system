package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Entities.BlacklistEntry;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import com.example.librarymanagementsystem.Services.BlacklistService;
import com.example.librarymanagementsystem.Services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;
    private final UserServices userServices;

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("/blacklist")
    public String showBlacklist(Model model) {
        List<BlacklistEntry> entries = blacklistService.getActiveBlacklistedUsers();
        model.addAttribute("entries", entries);
        return "admin/blacklist";
    }

    @PostMapping("/ban/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','LIBRARIAN')")
    public ResponseEntity<?> banUser(@PathVariable UUID userId, @RequestParam String reason) {
        try {
            User user = userServices.getUserById(userId);
            blacklistService.banUserManually(user, reason);
            return ResponseEntity.ok("User banned successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка при блокировке пользователя: " + e.getMessage());
        }
    }

    @PostMapping("/unban/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','LIBRARIAN')")
    public ResponseEntity<?> unbanUser(@PathVariable UUID userId) {
        try {
            User user = userServices.getUserById(userId);
            blacklistService.unbanUser(user);
            return ResponseEntity.ok("User unbanned successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка при разблокировке пользователя: " + e.getMessage());
        }
    }
    @GetMapping("/users/search")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String searchUserByEmail(@RequestParam("email") String email, Model model) {
        try {
            Optional<User> optionalUser = userServices.getUserByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                boolean isBanned = userServices.isUserBanned(user.getId());
                model.addAttribute("foundUser", user);
                model.addAttribute("isBanned", isBanned);
            } else {
                model.addAttribute("errorMessage", "Пользователь с таким email не найден.");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при поиске пользователя: " + e.getMessage());
        }

        try {
            model.addAttribute("users", userServices.getAllUsers());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при получении списка пользователей: " + e.getMessage());
        }

        return "admin/admin-home";
    }



}

