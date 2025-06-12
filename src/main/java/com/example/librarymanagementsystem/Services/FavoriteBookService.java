package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Entities.Book;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс сервиса для управления избранными книгами пользователей.
 */
public interface FavoriteBookService {

    /**
     * Добавляет книгу в избранное для указанного пользователя.
     * @param userId ID пользователя.
     * @param bookId ID книги.
     */
    void addBookToFavorites(UUID userId, UUID bookId);

    /**
     * Удаляет книгу из избранного для указанного пользователя.
     * @param userId ID пользователя.
     * @param bookId ID книги.
     */
    void removeBookFromFavorites(UUID userId, UUID bookId);

    /**
     * Возвращает список всех избранных книг для пользователя.
     * @param userId ID пользователя.
     * @return Список сущностей Book.
     */
    List<Book> getFavoriteBooksForUser(UUID userId);

    /**
     * Проверяет, находится ли книга в избранном у пользователя.
     * @param userId ID пользователя.
     * @param bookId ID книги.
     * @return true, если книга в избранном, иначе false.
     */
    boolean isBookInFavorites(UUID userId, UUID bookId);
}