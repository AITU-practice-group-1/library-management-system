package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.DTOs.book.TopFavoriteBookDTO;
import com.example.librarymanagementsystem.DTOs.book.TopRatedBookDTO;
import com.example.librarymanagementsystem.Services.BookService;
import com.example.librarymanagementsystem.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    private final BookService bookService;
    private final UserServices userServices;

    @Autowired
    public MainController(BookService bookService, UserServices userServices) {
        this.bookService = bookService;
        this.userServices = userServices;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<TopRatedBookDTO> topRatedBooks = bookService.getTopRatedBooks(10);
        model.addAttribute("topRatedBooks", topRatedBooks);
        List<TopFavoriteBookDTO> topFavoritedBooks = bookService.getTopFavoriteBooks(10);
        model.addAttribute("topFavoritedBooks", topFavoritedBooks);
        return "index";
    }

    @GetMapping("/error")
    public String error(){
        return "error";
    }
    @GetMapping("/error/403")
    public String error403(){
        return "access-denied";
    }
}
