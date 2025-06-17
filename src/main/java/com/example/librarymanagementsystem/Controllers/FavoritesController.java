package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Entities.Favorites;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.FavoritesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.example.librarymanagementsystem.Repositories.UserRepository;


@Controller
@RequestMapping("/favorites")
public class FavoritesController {
    private final FavoritesService favoritesService;
    private final UserRepository userRepository;

    public FavoritesController(FavoritesService favoritesService, UserRepository userRepository) {
        this.favoritesService = favoritesService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listFavorites(@RequestParam UUID userId, Model model) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        List<Favorites> favorites = favoritesService.getFavoritesByUser(user);

        model.addAttribute("favorites", favorites);
        model.addAttribute("userId", userId);
        return "favorites";
    }

    @PostMapping("/add")
    public String addFavorite(@RequestParam UUID userId, @RequestParam UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        favoritesService.addFavorite(user, bookId);
        return "redirect:/favorites?userId=" + userId;
    }

    @PostMapping("/remove/{bookId}")
    public String removeFavorite(@RequestParam UUID userId, @PathVariable UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        favoritesService.removeFavorite(user, bookId);
        return "redirect:/favorites?userId=" + userId;
    }
}