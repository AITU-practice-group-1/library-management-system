package com.example.librarymanagementsystem.Services.impl;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.Entities.Book;
import com.example.librarymanagementsystem.Entities.FavoriteBook;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.Repositories.FavoriteBookRepository;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import com.example.librarymanagementsystem.Services.FavoriteBookService;
import com.example.librarymanagementsystem.Services.UserServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) // Большинство методов только для чтения, поэтому ставим readOnly=true по умолчанию
public class FavoriteBookServiceImpl implements FavoriteBookService {

    private final FavoriteBookRepository favoriteBookRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserServices userServices;

    // Используем конструктор для внедрения зависимостей
    public FavoriteBookServiceImpl(FavoriteBookRepository favoriteBookRepository, UserRepository userRepository, BookRepository bookRepository, UserServices userServices) {
        this.favoriteBookRepository = favoriteBookRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.userServices = userServices;
    }

    @Override
    @Transactional // Переопределяем транзакцию, так как этот метод изменяет данные
    public void addBookToFavorites(UUID userId, UUID bookId) {
        try {
            UserDTO user = userServices.getAuhtenticatedUser();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книга с ID " + bookId + " не найдена"));

        // Проверяем, не добавлена ли уже книга в избранное
        if (favoriteBookRepository.existsByUserAndBook(user, book)) {
            // Можно бросить исключение или просто ничего не делать
            // throw new IllegalStateException("Книга уже в избранном");
            return;
        }

        FavoriteBook favoriteBook = new FavoriteBook();
        favoriteBook.setUser(user);
        favoriteBook.setBook(book);

        favoriteBookRepository.save(favoriteBook);
    }

    @Override
    @Transactional // Этот метод также изменяет данные
    public void removeBookFromFavorites(UUID userId, UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книга с ID " + bookId + " не найдена"));

        // Используем метод репозитория для эффективного удаления
        favoriteBookRepository.deleteByUserAndBook(user, book);
    }

    @Override
    public List<Book> getFavoriteBooksForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));

        return favoriteBookRepository.findByUser(user)
                .stream()
                .map(FavoriteBook::getBook) // Преобразуем Stream<FavoriteBook> в Stream<Book>
                .collect(Collectors.toList());
    }

    @Override
    public boolean isBookInFavorites(UUID userId, UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книга с ID " + bookId + " не найдена"));

        return favoriteBookRepository.existsByUserAndBook(user, book);
    }
}
