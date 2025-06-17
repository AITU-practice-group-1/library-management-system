package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.DTOs.book.TopFavoriteBookDTO;
import com.example.librarymanagementsystem.DTOs.book.TopRatedBookDTO;
import com.example.librarymanagementsystem.util.Genre;
import com.example.librarymanagementsystem.DTOs.book.BookDetailViewDTO;
import com.example.librarymanagementsystem.Entities.Genre;
import com.example.librarymanagementsystem.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface BookService {
    BookDTO createBook(BookDTO bookDTO);
    Page<BookDTO> findPaginatedAndFiltered(String keyword, Genre genre, Pageable pageable);
    BookDTO getBookById(UUID id);
    List<BookDTO> getAllBooks();
    BookDTO updateBook(UUID id, BookDTO bookDTO);
    void deleteBook(UUID id);
    List<BookDTO> findAllAvailableBooks();
    void lendBook(UUID bookId);
    void returnBook(UUID bookId);

    @Transactional
    void addRatingToBook(UUID bookId, int rating);

    void updateBookRating (UUID bookId, Integer rating);

    @Transactional
    void updateBookRating(UUID bookId, int oldRating, int newRating);

    @Transactional
    void removeRatingFromBook(UUID bookId, int ratingToRemove);

    void recalculateBookRatingOnUpdate(UUID bookId, int oldRating, int newRating);
    void recalculateBookRatingOnDelete(UUID bookId, int ratingToRemove);
    List<TopRatedBookDTO> getTopRatedBooks(int limit);
    List<TopFavoriteBookDTO> getTopFavoriteBooks(int limit);


}
