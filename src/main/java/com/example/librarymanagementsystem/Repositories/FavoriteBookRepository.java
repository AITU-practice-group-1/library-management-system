package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.Book;
import com.example.librarymanagementsystem.Entities.FavoriteBook;
import com.example.librarymanagementsystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью FavoriteBook.
 * Предоставляет методы для взаимодействия с таблицей 'favoritebooks' в базе данных.
 */
@Repository
public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, UUID> {

    /**
     * Находит все избранные книги для конкретного пользователя.
     * @param user Пользователь, чьи избранные книги нужно найти.
     * @return Список объектов FavoriteBook.
     */
    List<FavoriteBook> findByUser(User user);

    /**
     * Находит запись в избранном по пользователю и книге.
     * Используется для проверки, добавлена ли уже книга в избранное.
     * @param user Пользователь.
     * @param book Книга.
     * @return Optional, содержащий FavoriteBook, если найдено.
     */
    Optional<FavoriteBook> findByUserAndBook(User user, Book book);

    /**
     * Удаляет книгу из избранного для конкретного пользователя.
     * @param user Пользователь.
     * @param book Книга.
     */
    void deleteByUserAndBook(User user, Book book);

    /**
     * Проверяет существование записи по пользователю и книге.
     * Более эффективный способ для проверки, чем findByUserAndBook, если сама сущность не нужна.
     * @param user Пользователь.
     * @param book Книга.
     * @return true, если книга в избранном у пользователя, иначе false.
     */
    boolean existsByUserAndBook(User user, Book book);
}
