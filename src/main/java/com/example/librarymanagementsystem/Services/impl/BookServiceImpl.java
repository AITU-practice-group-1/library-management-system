package com.example.librarymanagementsystem.Services.impl;

import com.example.librarymanagementsystem.DTOs.book.*;
import com.example.librarymanagementsystem.Entities.Book;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.Services.BookService;
import com.example.librarymanagementsystem.exceptions.BookNotFoundException;
import com.example.librarymanagementsystem.mapper.BookMapper;
import com.example.librarymanagementsystem.util.Genre;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public BookResponseDTO createBook(BookCreateDTO bookDTO) {
        if (bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
            throw new DataIntegrityViolationException("A book with this ISBN already exists.");
        }
        Book book = bookMapper.toEntity(bookDTO);
        // On creation, all copies are available.
        book.setAvailableCopies(book.getTotalCopies());
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponseDTO(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(UUID id) {
        return bookRepository.findById(id)
                .map(bookMapper::toResponseDTO)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
    }

    @Override
    @Transactional
    public BookResponseDTO updateBook(UUID id, BookUpdateDTO bookDTO) {
        if (bookRepository.existsByIsbnAndIdNot(bookDTO.getIsbn(), id)) {
            throw new DataIntegrityViolationException("Another book with this ISBN already exists.");
        }

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        int checkedOutCopies = existingBook.getTotalCopies() - existingBook.getAvailableCopies();
        if (bookDTO.getTotalCopies() < checkedOutCopies) {
            throw new IllegalStateException("Total copies cannot be less than the number of currently checked-out or reserved books (" + checkedOutCopies + ").");
        }

        bookMapper.updateEntityFromDto(bookDTO, existingBook);
        existingBook.setAvailableCopies(bookDTO.getTotalCopies() - checkedOutCopies);

        Book updatedBook = bookRepository.save(existingBook);
        return bookMapper.toResponseDTO(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Cannot delete. Book not found with ID: " + id));

        if (book.getTotalCopies() != book.getAvailableCopies()) {
            throw new IllegalStateException("Cannot delete a book that has copies currently on loan or reserved.");
        }
        bookRepository.delete(book);
    }

    @Override
    @Transactional
    public void addRatingToBook(UUID bookId, int rating) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));
        book.addRating(rating);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void updateBookRating(UUID bookId, int oldRating, int newRating) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));
        book.updateRating(oldRating, newRating);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void removeRatingFromBook(UUID bookId, int ratingToRemove) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));
        book.removeRating(ratingToRemove);
        bookRepository.save(book);
    }

@Override
@Transactional
public void recalculateBookRatingOnDelete(UUID bookId, int ratingToRemove) {
    Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

    long newRatingSum = book.getRatingSum() - ratingToRemove;
    long newRatingCount = book.getRatingCount() - 1;

    book.setRatingSum(newRatingSum);
    book.setRatingCount(newRatingCount);

    if (newRatingCount > 0) {
        BigDecimal average = BigDecimal.valueOf(newRatingSum)
                .divide(BigDecimal.valueOf(newRatingCount), 2, RoundingMode.HALF_UP);
        book.setRatingAverage(average);
    } else {
        book.setRatingAverage(BigDecimal.ZERO);
    }
}
    @Override
    public Page<BookResponseDTO> findPaginatedAndFiltered(String keyword, Genre genre, Pageable pageable) {
        Specification<Book> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String lowerCaseKeyword = "%" + keyword.toLowerCase().trim() + "%";
                Predicate titleLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), lowerCaseKeyword);
                Predicate authorLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), lowerCaseKeyword);
                predicates.add(criteriaBuilder.or(titleLike, authorLike));
            }

            if (genre != null) {
                predicates.add(criteriaBuilder.equal(root.get("genre"), genre));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return bookRepository.findAll(spec, pageable).map(bookMapper::toResponseDTO);
    }

    public List<TopRatedBookDTO> getTopRatedBooks(int limit) {

        List<Object[]> results = bookRepository.findTopRatedBooks(limit);

        return results.stream().map(result -> {

            TopRatedBookDTO dto = new TopRatedBookDTO();

            dto.setId((UUID) result[0]);

            dto.setTitle((String) result[1]);

            dto.setRatingAverage(((Number) result[2]).doubleValue());

            return dto;

        }).collect(Collectors.toList());
    }

    public List<TopFavoriteBookDTO> getTopFavoriteBooks(int limit) {

        List<Object[]> results = bookRepository.findTopFavoriteBooks(limit);

        return results.stream().map(result -> {

            TopFavoriteBookDTO dto = new TopFavoriteBookDTO();

            dto.setId((UUID) result[0]);

            dto.setTitle((String) result[1]);

            dto.setFavoriteCount(((Number) result[2]).intValue());

            return dto;

        }).collect(Collectors.toList());

    }
}