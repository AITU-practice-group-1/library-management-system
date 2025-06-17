package com.example.librarymanagementsystem.Services.impl;

import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.DTOs.book.BookDetailViewDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackResponseDTO;
import com.example.librarymanagementsystem.Entities.Book;
import com.example.librarymanagementsystem.Entities.Genre;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.Services.BookService;
import com.example.librarymanagementsystem.Services.FavoriteBookService;
import com.example.librarymanagementsystem.Services.FeedbackService;
import com.example.librarymanagementsystem.exceptions.BookNotFoundException;
import com.example.librarymanagementsystem.mapper.BookMapper;
import com.example.librarymanagementsystem.mapper.BookMapperImpl;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Optional;
import java.util.UUID;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    private final BookMapperImpl mapper;


    @Override
    @Transactional
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

            return criteriaBuilder.conjunction();
        };
        return bookRepository.findAll(spec, pageable).map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookById(UUID id) {
        return bookRepository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookDTO updateBook(UUID id, BookDTO bookDTO) {
        if (bookRepository.existsByIsbnAndIdNot(bookDTO.getIsbn(), id)) {
            throw new DataIntegrityViolationException("Another book with this ISBN already exists.");
        }

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        int booksCheckedOut = existingBook.getTotalCopies() - existingBook.getAvailableCopies();
        if (bookDTO.getTotalCopies() < booksCheckedOut) {
            throw new IllegalStateException("Total copies cannot be less than the number of currently checked-out books.");
        }

        // Use the mapper to update the existing entity to avoid manual setters
        mapper.updateEntityFromDto(bookDTO, existingBook);
        existingBook.setAvailableCopies(bookDTO.getTotalCopies() - booksCheckedOut);

        Book updatedBook = bookRepository.save(existingBook);
        return mapper.toDTO(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(UUID id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Cannot delete. Book not found with ID: " + id);
        }
        bookRepository.deleteById(id);
    }



    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> findAllAvailableBooks() {
        return bookRepository.findAllAvailableBooks().stream().map(mapper::toDTO).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void lendBook(UUID bookId) {
        int updatedRows = bookRepository.decrementAvailableCopiesById(bookId);
        if (updatedRows == 0) {
            if (!bookRepository.existsById(bookId)) {
                throw new BookNotFoundException("Book not found with ID: " + bookId);
            }
            throw new IllegalStateException("No available copies of this book to lend.");
        }
    }

    @Override
    @Transactional
    public void returnBook(UUID bookId) {
        int updatedRows = bookRepository.incrementAvailableCopiesById(bookId);
        if (updatedRows == 0) {
            throw new BookNotFoundException("Cannot return a book that does not exist with ID: " + bookId);
        }
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
    public void updateBookRating(UUID bookId, Integer rating) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        System.out.println(book.toString());
        book.addRating(rating);
        System.out.println(book);
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
    public void recalculateBookRatingOnUpdate(UUID bookId, int oldRating, int newRating) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));


        long newRatingSum = book.getRatingSum() - oldRating + newRating;
        book.setRatingSum(newRatingSum);

        if (book.getRatingCount() > 0) {
            BigDecimal average = BigDecimal.valueOf(newRatingSum)
                    .divide(BigDecimal.valueOf(book.getRatingCount()), 2, RoundingMode.HALF_UP);
            book.setRatingAverage(average);
        }

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

        bookRepository.save(book);
    }

}
