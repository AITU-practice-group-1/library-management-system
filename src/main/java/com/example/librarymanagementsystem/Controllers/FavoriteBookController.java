package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Entities.Book;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.FavoriteBookService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/favoriteBooks")
public class FavoriteBookController {

    private final FavoriteBookService favoriteBookService;

    public FavoriteBookController(FavoriteBookService favoriteBookService) {
        this.favoriteBookService = favoriteBookService;
    }

    @GetMapping
    public String listFavoritesByUser(Model model, @AuthenticationPrincipal User user) {
        List<Book> favoriteBooks = favoriteBookService.getFavoriteBooksForUser(user.getId());
        model.addAttribute("favoriteBooks", favoriteBooks);
        return "favoriteBooks/favoriteBooks";
    }

    @PostMapping("/add")
    public String addToFavorites(@RequestParam("bookId") UUID bookId,
                                 @AuthenticationPrincipal User user,
                                 RedirectAttributes redirectAttributes) {
        try {
            favoriteBookService.addBookToFavorites(user.getId(), bookId);
            redirectAttributes.addFlashAttribute("successMessage", "Книга успешно добавлена в избранное!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось добавить книгу в избранное. " + e.getMessage());
        }


        return "redirect:/books/" + bookId;
    }


    @PostMapping("/remove")
    public String removeFromFavorites(@RequestParam("bookId") UUID bookId,
                                      // This parameter is needed to determine what URL redirect to.
                                      // Because this endpoint is used both all favorites and a detailed book page.
                                      @RequestParam(value = "redirectUrl", defaultValue = "/favoriteBooks") String redirectUrl,
                                      @AuthenticationPrincipal User user,
                                      RedirectAttributes redirectAttributes) {
        try {
            favoriteBookService.removeBookFromFavorites(user.getId(), bookId);
            redirectAttributes.addFlashAttribute("successMessage", "Book removed from favorites.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove book. " + e.getMessage());
        }

        return "redirect:" + redirectUrl;
    }
}
