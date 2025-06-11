package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.Entities.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

}
