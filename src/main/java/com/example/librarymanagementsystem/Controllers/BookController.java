package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.Entities.Genre;
import com.example.librarymanagementsystem.Services.impl.BookServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookServiceImpl bookService;

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String CreateBookPage(Model model) {
        model.addAttribute("book", new BookDTO());
        model.addAttribute("allGenres", Genre.values());
        return "books/create";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String createBook(@Valid @ModelAttribute("book") BookDTO bookDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allGenres", Genre.values());
            return "books/create";
        }
        try {
            bookService.createBook(bookDTO);
        } catch (DataIntegrityViolationException e) {
            FieldError error = new FieldError(
                    "book",
                    "isbn",
                    "This ISBN already exists in the system."
            );
            bindingResult.addError(error);
            model.addAttribute("allGenres", Genre.values());
            return "books/create";
        }

        return "redirect:/books/manage";

    }

    @GetMapping("/{id}")
    public String ShowBook(@PathVariable UUID id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        // You can add other model attributes here like:
        // model.addAttribute("reviews", reviewService.findByBook(id));
        // model.addAttribute("isFavorite", favoriteService.isFavorite(id, currentUser));
        return "books/book"; // Renders the public-facing page
    }

    // NEW --- Add this method for the ADMIN/LIBRARIAN detailed view
    @GetMapping("/{id}/manage")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String ShowBookDetailsForAdmin(@PathVariable UUID id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "books/admin_book";
    }

    @GetMapping
    public String ShowAllBooks(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "genre", required = false) Genre genre,
                               @PageableDefault(size = 10, sort = "title") Pageable pageable,
                               Model model) {

        Page<BookDTO> books = bookService.findPaginatedAndFiltered(keyword, genre, pageable);

        model.addAttribute("bookPage", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("genre", genre);
        model.addAttribute("allGenres", Genre.values());
        // Simple way to pass sort info to Thymeleaf
        model.addAttribute("sortField", pageable.getSort().get().findFirst().get().getProperty());
        model.addAttribute("sortDir", pageable.getSort().get().findFirst().get().getDirection().name());

        return "books/books"; // This maps to the public view
    }

    // NEW --- Recommended method for the management page
    @GetMapping("/manage")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String showAllBooksForAdmins(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "genre", required = false) Genre genre,
                                        @PageableDefault(size = 10, sort = "title") Pageable pageable,
                                        Model model) {

        Page<BookDTO> books = bookService.findPaginatedAndFiltered(keyword, genre, pageable);

        model.addAttribute("bookPage", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("genre", genre);
        model.addAttribute("allGenres", Genre.values());
        model.addAttribute("sortField", pageable.getSort().get().findFirst().get().getProperty());
        model.addAttribute("sortDir", pageable.getSort().get().findFirst().get().getDirection().name());

        return "books/admin_books"; // This maps to the new admin/librarian view
    }



    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String UpdateBookPage(@PathVariable UUID id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        model.addAttribute("allGenres", Genre.values());
        return "books/edit";
    }

    @PostMapping("/{id}/update") // Using POST for form submission simplicity
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Validated
    public String UpdateBook(@PathVariable UUID id,
                             @Valid @ModelAttribute("book") BookDTO bookDTO,
                             BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allGenres", Genre.values());
            return "books/edit";
        }
        try {
            bookService.updateBook(id, bookDTO);
        } catch (DataIntegrityViolationException e) {
            FieldError error = new FieldError(
                    "book",
                    "isbn",
                    "This ISBN already exists in the system."
            );
            bindingResult.addError(error);
            model.addAttribute("book", bookDTO);
            model.addAttribute("allGenres", Genre.values());
            return "books/edit";
        }
        return "redirect:/books/manage";
    }


    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String DeleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return "redirect:/books/manage";
    }
}
