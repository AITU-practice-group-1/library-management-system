package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.DTOs.book.TopFavoriteBookDTO;
import com.example.librarymanagementsystem.DTOs.book.TopRatedBookDTO;
import com.example.librarymanagementsystem.Entities.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
    void updateBookRating (UUID bookId, Integer rating);
    void recalculateBookRatingOnUpdate(UUID bookId, int oldRating, int newRating);
    void recalculateBookRatingOnDelete(UUID bookId, int ratingToRemove);
    List<TopRatedBookDTO> getTopRatedBooks(int limit);
    List<TopFavoriteBookDTO> getTopFavoriteBooks(int limit);
}
