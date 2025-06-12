package com.example.librarymanagementsystem.Services.impl;

import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.Entities.Book;
import com.example.librarymanagementsystem.Entities.Genre;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.Services.BookService;
import com.example.librarymanagementsystem.mapper.BookMapper;
import com.example.librarymanagementsystem.mapper.BookMapperImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapperImpl mapper;


    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        Book book = mapper.toEntity(bookDTO);
        book.setAvailableCopies(book.getTotalCopies());
        return mapper.toDTO(bookRepository.save(book));
    }

    @Override
    public Page<BookDTO> findPaginatedAndFiltered(String keyword, Genre genre, Pageable pageable) {
        Specification<Book> spec = (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                var titleLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
                var authorLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + keyword.toLowerCase() + "%");
                predicates.add(criteriaBuilder.or(titleLike, authorLike));
            }

            if (genre != null) {
                predicates.add(criteriaBuilder.equal(root.get("genre"), genre));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        return bookPage.map(mapper::toDTO);
    }

    @Override
    public BookDTO getBookById(UUID id) {
        Book book = bookRepository.findById(id).orElse(null);
        return book != null ? mapper.toDTO(book) : null;
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO updateBook(UUID id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        book = mapper.toEntity(bookDTO);
        return mapper.toDTO(bookRepository.save(book));
    }

    @Override
    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> findAllAvailableBooks() {
        return bookRepository.findAllAvailableBooks().stream().map(mapper::toDTO).collect(Collectors.toList());
    }


    @Transactional
    public void lendBook(UUID bookId) {
        // First, ensure the book exists.
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));

        // Business logic: Check if the book is available.
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies of '" + book.getTitle() + "' to lend.");
        }

        // If checks pass, call the repository to update the database.
        bookRepository.decrementAvailableCopiesById(bookId);
    }

    @Transactional
    public void returnBook(UUID bookId) {
        // Ensure the book exists before trying to increment.
        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Cannot return a book that does not exist with ID: " + bookId);
        }

        // Call the repository to update the database.
        bookRepository.incrementAvailableCopiesById(bookId);
    }
}