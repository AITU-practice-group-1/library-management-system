package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Entities.BlacklistEntry;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.BlacklistService;
import com.example.librarymanagementsystem.Services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;
    private final UserServices userServices;

    @GetMapping("/blacklist")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String showBlacklist(@RequestParam(required = false) String search, Model model) {
        List<BlacklistEntry> allEntries = blacklistService.findAll();

        List<BlacklistEntry> bannedEntries = allEntries.stream()
                .filter(e -> e.getUser().isBanned())
                .collect(Collectors.toList());

        List<BlacklistEntry> historyEntries = allEntries.stream()
                .filter(e -> !e.getUser().isBanned() || e.isResolved())
                .collect(Collectors.toList());

        if (search != null && !search.isBlank()) {
            bannedEntries = bannedEntries.stream()
                    .filter(e -> e.getUser().getEmail().toLowerCase().contains(search.toLowerCase()) ||
                            e.getUser().getFirstName().toLowerCase().contains(search.toLowerCase()) ||
                            e.getUser().getLastName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());

            historyEntries = historyEntries.stream()
                    .filter(e -> e.getUser().getEmail().toLowerCase().contains(search.toLowerCase()) ||
                            e.getUser().getFirstName().toLowerCase().contains(search.toLowerCase()) ||
                            e.getUser().getLastName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("bannedEntries", bannedEntries);
        model.addAttribute("historyEntries", historyEntries);
        model.addAttribute("search", search);
        return "admin/blacklist";
    }

    @PostMapping("/ban/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String banUser(@PathVariable UUID id, @RequestParam String reason) throws Exception {
        User user = userServices.getUserById(id);
        if (!user.isBanned()) {
            blacklistService.banUserManually(user, reason);
        }
        return "redirect:/blacklist";
    }

    @PostMapping("/unban/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String unbanUser(@PathVariable UUID userId) {
        try {
            User user = userServices.getUserById(userId);
            blacklistService.unbanUser(user);
        } catch (Exception e) {
            // можно добавить flash сообщение
        }
        return "redirect:/blacklist";
    }
}


