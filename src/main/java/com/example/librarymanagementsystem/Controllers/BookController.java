package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.Entities.Book;
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
//    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String createBook(@Valid @ModelAttribute("book") BookDTO bookDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
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
            return "books/create";
        }

        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String ShowBook(@PathVariable UUID id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "books/book";
    }

    @GetMapping
    public String ShowAllBooks(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "genre", required = false) Genre genre,
                               @PageableDefault(size = 10, sort = "title") Pageable pageable,
                               Model model) {

        Page<BookDTO> books = bookService.findPaginatedAndFiltered(keyword, genre, pageable);

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("genre", genre);
        model.addAttribute("allGenres", Genre.values());
        model.addAttribute("sort", pageable.getSort().toString().replace(": ", ","));

        return "books/books";
    }

    @GetMapping("/{id}/edit")
    public String UpdateBookPage(@PathVariable UUID id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String UpdateBook(@ModelAttribute("book") @Valid BookDTO bookDTO,
                             @PathVariable UUID id,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/update";
        }
        bookService.updateBook(id, bookDTO);
        return "redirect:/books";
    }

//    @PreAuthorize("hasRole()")
    @DeleteMapping("/{id}")
    public String DeleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }


}
