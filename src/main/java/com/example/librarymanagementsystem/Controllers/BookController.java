package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.book.*;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackRequestDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackResponseDTO;
import com.example.librarymanagementsystem.exceptions.BookNotFoundException;
import com.example.librarymanagementsystem.util.Genre;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final FeedbackService feedbackService;
    private final FavoriteBookService favoriteBookService;
    private final ReservationsServices reservationsServices;

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String showCreateBookPage(Model model) {
        if (!model.containsAttribute("book")) {
            model.addAttribute("book", new BookCreateDTO());
        }
        model.addAttribute("allGenres", Genre.values());
        return "books/create";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String createBook(@Valid @ModelAttribute("book") BookCreateDTO bookDTO,
                             BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, @RequestParam MultipartFile imageFile) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allGenres", Genre.values());
            return "books/create";
        }
        try {
            if(imageFile != null)
            {
                bookService.createBook(bookDTO, imageFile);
            }
            else
            {
                bookService.createBook(bookDTO);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Book created successfully!");
            return "redirect:/books/manage";
        } catch (DataIntegrityViolationException | IOException e) {
            bindingResult.addError(new FieldError("book", "isbn", "This ISBN already exists."));
            model.addAttribute("allGenres", Genre.values());
            return "books/create";
        }
    }

    @GetMapping("/{id}")
    public String showBook(@PathVariable UUID id, Model model, @AuthenticationPrincipal User user,
                           @RequestParam(defaultValue = "createdAt", name = "sort") String sortField,
                           @RequestParam(defaultValue = "desc", name = "dir") String sortDir,
                           @PageableDefault(size = 5) Pageable pageable) {

        BookResponseDTO bookDTO = bookService.getBookById(id);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortField);
        Pageable reviewPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<FeedbackResponseDTO> reviews = feedbackService.getByBookId(id, reviewPageable);

        boolean isFavorite = false;
        Optional<FeedbackResponseDTO> userReview = Optional.empty();

        if (user != null) {
            isFavorite = favoriteBookService.isBookInFavorites(user.getId(), id);
            userReview = feedbackService.getByBookIdAndUserId(id, user.getId());
        }

        BookDetailViewDTO bookDetail = BookDetailViewDTO.builder()
                .book(bookDTO)
                .isFavorite(isFavorite)
                .reviews(reviews)
                .userReview(userReview)
                .hasUserReviewed(userReview.isPresent())
                .build();

        bookDetail.getUserReview().ifPresent(review -> {
            if (!model.containsAttribute("userReview")) {
                model.addAttribute("userReview", review);
            }
        });

        if (!model.containsAttribute("feedbackRequest")) {
            model.addAttribute("feedbackRequest", new FeedbackRequestDTO());
        }

        Set<UUID> reservedBookIds = reservationsServices.getActiveReservationBookIdsByUser(user);
        model.addAttribute("reservedBookIds", reservedBookIds);

        model.addAttribute("book", bookDetail.getBook());
        model.addAttribute("isFavorite", bookDetail.isFavorite());
        model.addAttribute("reviews", bookDetail.getReviews());
        model.addAttribute("hasUserReviewed", bookDetail.isHasUserReviewed());
        bookDetail.getUserReview().ifPresent(review -> model.addAttribute("userReview", review));

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        return "books/book";
    }

    @GetMapping("/{id}/manage")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String ShowBookDetailsForAdmin(@PathVariable UUID id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "books/admin_book";
    }

    @GetMapping
    public String showAllBooks(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Genre genre,
                               @AuthenticationPrincipal User user,
                               @PageableDefault(size = 12, sort = "title") Pageable pageable,
                               Model model) {
        populateBookListPageModel(keyword, genre, pageable, model);
        Set<UUID> reservedBookIds = reservationsServices.getActiveReservationBookIdsByUser(user);
        model.addAttribute("reservedBookIds", reservedBookIds);
        return "books/books";
    }

    private void populateBookListPageModel(String keyword, Genre genre, Pageable pageable, Model model) {
        Page<BookResponseDTO> bookPage = bookService.findPaginatedAndFiltered(keyword, genre, pageable);
        model.addAttribute("bookPage", bookPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("genre", genre);
        model.addAttribute("allGenres", Genre.values());

        Sort.Order sortOrder = pageable.getSort().stream().findFirst().orElse(Sort.Order.by("title"));
        model.addAttribute("sortField", sortOrder.getProperty());
        model.addAttribute("sortDir", sortOrder.getDirection().name().toLowerCase());
    }


    @GetMapping("/manage")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public String showAllBooksForAdmins(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Genre genre,
                                        @PageableDefault(size = 10, sort = "title") Pageable pageable,
                                        Model model) {
        populateBookListPageModel(keyword, genre, pageable, model);
        return "books/admin_books";
    }



    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String showUpdateBookPage(@PathVariable UUID id, Model model) {
        if (!model.containsAttribute("book")) {
            model.addAttribute("book", bookService.getBookById(id)); // Send ResponseDTO to pre-fill form
        }
        model.addAttribute("allGenres", Genre.values());
        return "books/edit";
    }


    @PostMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String updateBook(@PathVariable UUID id,
                             @Valid @ModelAttribute("book") BookUpdateDTO bookDTO,
                             BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, @RequestParam MultipartFile imageFile) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allGenres", Genre.values());
            return "books/edit";
        }
        try {
            if(imageFile != null)
            {
                bookService.updateBook(id, bookDTO, imageFile);
            }
            else {
                bookService.updateBook(id, bookDTO);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully!");
            return "redirect:/books/manage";
        } catch (DataIntegrityViolationException e) {
            bindingResult.addError(new FieldError("book", "isbn", "This ISBN already exists."));
            model.addAttribute("allGenres", Genre.values());
            return "books/edit";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/books/" + id + "/edit";
        }
    }



    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')") // Deleting should be restricted, perhaps only to Admins
    public String deleteBook(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully.");
        } catch (IllegalStateException | BookNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/books/manage";
    }
}
