package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.Entities.Book;
import com.example.librarymanagementsystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAllAvailableBooks();

    @Modifying
    @Query("UPDATE Book b SET b.availableCopies = b.availableCopies - 1 WHERE b.id = :id AND b.availableCopies > 0")
    int decrementAvailableCopiesById(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Book b SET b.availableCopies = b.availableCopies + 1 WHERE b.id = :id")
    int incrementAvailableCopiesById(@Param("id") UUID id);

    @Query("SELECT b.id, b.title, b.ratingAverage " +
            "FROM Book b " +
            "WHERE b.ratingCount > 0 " +
            "ORDER BY b.ratingAverage DESC " +
            "LIMIT :limit")
    List<Object[]> findTopRatedBooks(int limit);

    @Query("SELECT b.id, b.title, COUNT(fb.id) as favoriteCount " +
            "FROM Book b " +
            "LEFT JOIN FavoriteBook fb ON b.id = fb.book.id " +
            "GROUP BY b.id, b.title " +
            "ORDER BY favoriteCount DESC " +
            "LIMIT :limit")
    List<Object[]> findTopFavoriteBooks(int limit);

    /**
     * Finds a book by its ISBN. Using Optional is a best practice.
     *
     * @param isbn The 13-digit ISBN.
     * @return An Optional containing the book if found.
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Checks if an ISBN exists for any book other than the one with the given ID.
     * Useful for validation during an update operation.
     *
     * @param isbn The ISBN to check.
     * @param id The ID of the book to exclude from the check.
     * @return true if the ISBN is used by another book, false otherwise.
     */
    boolean existsByIsbnAndIdNot(String isbn, UUID id);
}