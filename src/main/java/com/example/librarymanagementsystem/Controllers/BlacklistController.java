package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Entities.BlacklistEntry;
import com.example.librarymanagementsystem.Services.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("/blacklist")
    public String showBlacklist(Model model) {
        List<BlacklistEntry> entries = blacklistService.getActiveBlacklistedUsers();
        model.addAttribute("entries", entries);
        return "blacklist/blacklist";
    }
}

