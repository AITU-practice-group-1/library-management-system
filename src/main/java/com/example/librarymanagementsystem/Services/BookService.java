package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.book.*;
import com.example.librarymanagementsystem.util.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface BookService {
    BookResponseDTO createBook(BookCreateDTO bookDTO);
    BookResponseDTO getBookById(UUID id);
    Page<BookResponseDTO> findPaginatedAndFiltered(String keyword, Genre genre, Pageable pageable);
    BookResponseDTO updateBook(UUID id, BookUpdateDTO bookDTO);
    void deleteBook(UUID id);

    // Methods for rating updates, to be called by FeedbackService
    void addRatingToBook(UUID bookId, int rating);
    void updateBookRating(UUID bookId, int oldRating, int newRating);
    void removeRatingFromBook(UUID bookId, int ratingToRemove);

    void recalculateBookRatingOnDelete(UUID bookId, int ratingToRemove);
    List<TopRatedBookDTO> getTopRatedBooks(int limit);
    List<TopFavoriteBookDTO> getTopFavoriteBooks(int limit);

}