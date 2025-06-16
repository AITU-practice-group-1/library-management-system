package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackRequestDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackResponseDTO;
import com.example.librarymanagementsystem.Entities.Genre;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.FavoriteBookService;
import com.example.librarymanagementsystem.Services.FeedbackService;
import com.example.librarymanagementsystem.Services.impl.BookServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookServiceImpl bookService;
    private final FeedbackService feedbackService;
    private final FavoriteBookService favoriteBookService;

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
    public String showBook(@PathVariable UUID id, Model model, @AuthenticationPrincipal User user,
                           @RequestParam(defaultValue = "createdAt") String sort,
                           @RequestParam(defaultValue = "desc") String dir,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "5") int size) throws Exception {
        System.out.println("BookDTO book = bookService.getBookById(id);");
        BookDTO book = bookService.getBookById(id);
        if (book == null) {
            return "books/book";
        }
        model.addAttribute("book", book);
        System.out.println("book.getRatingCount() = " + book.getRatingCount());
        System.out.println("book.getRatingSum() = " + book.getRatingSum());
        System.out.println("book.getRatingAverage() = " + book.getRatingAverage());

        // Favorite Status
        boolean isFavorite = false;
        if (user != null) {
            isFavorite = favoriteBookService.isBookInFavorites(user.getId(), id);
        }
        model.addAttribute("isFavorite", isFavorite);

        // Reviews & Feedback Logic
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));


        Page<FeedbackResponseDTO> reviews = null;

        reviews = feedbackService.getByBookId(id, pageable);

        model.addAttribute("reviews", reviews);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDir", dir);

        if (!model.containsAttribute("feedbackRequest")) {
            model.addAttribute("feedbackRequest", new FeedbackRequestDTO());
        }

        if (user != null) {
            Optional<FeedbackResponseDTO> userReview = feedbackService.getByBookIdAndUserId(id, user.getId());
            userReview.ifPresent(review -> {
                // Also add the 'userReview' object for the edit form
                if (!model.containsAttribute("userReview")) {
                    model.addAttribute("userReview", review);
                }
            });
            model.addAttribute("hasUserReviewed", userReview.isPresent());
        } else {
            model.addAttribute("hasUserReviewed", false);
        }

        // Add current role to model for debugging
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null && !auth.getAuthorities().isEmpty()) {
            model.addAttribute("currentRole", auth.getAuthorities().iterator().next().getAuthority());
        } else {
            model.addAttribute("currentRole", "No Role");
        }

        return "books/book";
    }

    @GetMapping("/{id}/manage")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String ShowBookDetailsForAdmin(@PathVariable UUID id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "books/admin_book";
    }

    @GetMapping
    public String ShowAllBooks(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "genre", required = false) String genre,
                               @PageableDefault(size = 10, sort = "title") Pageable pageable,
                               Model model) {

        Genre genreEnum = null;
        if (StringUtils.hasText(genre)) {
            try {
                genreEnum = Genre.valueOf(genre);
            } catch (IllegalArgumentException e) {
                // Ignore invalid genre string
            }
        }

        Page<BookDTO> books = bookService.findPaginatedAndFiltered(keyword, genreEnum, pageable);
        Optional<BookDTO> bookDTO = books.get().findFirst();
        if (bookDTO.isPresent()) {
            System.out.println(bookDTO);
        }
        model.addAttribute("bookPage", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("genre", genreEnum);
        model.addAttribute("allGenres", Genre.values());

        Sort.Order sortOrder = pageable.getSort().stream().findFirst().orElse(null);
        if (sortOrder != null) {
            model.addAttribute("sortField", sortOrder.getProperty());
            model.addAttribute("sortDir", sortOrder.getDirection().name());
        } else {
            model.addAttribute("sortField", "title");
            model.addAttribute("sortDir", "ASC");
        }

        return "books/books";
    }


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

        return "books/admin_books";
    }



    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String UpdateBookPage(@PathVariable UUID id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        model.addAttribute("allGenres", Genre.values());
        return "books/edit";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String UpdateBook(@PathVariable UUID id,
                             @Valid @ModelAttribute("book") BookDTO bookDTO,
                             BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.hasErrors());
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
