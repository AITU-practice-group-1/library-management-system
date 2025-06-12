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
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {

    /**
     * Finds all books where the number of available copies is greater than zero.
     *
     * @return A list of available books.
     */
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAllAvailableBooks();

    /**
     * Decrements the available copy count for a given book ID.
     * The operation will only succeed if there are available copies (>= 1).
     * The @Modifying annotation is required for any query that is not a SELECT.
     *
     * @param id The UUID of the book to update.
     * @return The number of rows affected (should be 1 if successful, 0 otherwise).
     */
    @Modifying
    @Query("UPDATE Book b SET b.availableCopies = b.availableCopies - 1 WHERE b.id = :id AND b.availableCopies > 0")
    int decrementAvailableCopiesById(@Param("id") UUID id);

    /**
     * Increments the available copy count for a given book ID.
     * This is typically used when a book is returned.
     * The @Modifying annotation is required for any query that is not a SELECT.
     *
     * @param id The UUID of the book to update.
     * @return The number of rows affected (should be 1 if successful).
     */
    @Modifying
    @Query("UPDATE Book b SET b.availableCopies = b.availableCopies + 1 WHERE b.id = :id")
    int incrementAvailableCopiesById(@Param("id") UUID id);
}