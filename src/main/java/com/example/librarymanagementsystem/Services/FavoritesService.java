package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Entities.Book;
import com.example.librarymanagementsystem.Entities.Favorites;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.Repositories.FavoritesRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final BookRepository bookRepository;

    public FavoritesService(FavoritesRepository favoritesRepository, BookRepository bookRepository) {
        this.favoritesRepository = favoritesRepository;
        this.bookRepository = bookRepository;
    }

    public void addFavorite(User user, UUID bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        Favorites favorite = new Favorites();
        favorite.setUser(user);
        favorite.setBook(book);
        favoritesRepository.save(favorite);
    }

    public List<Favorites> getFavoritesByUser(User user) {
        return favoritesRepository.findByUser(user);
    }

    @Transactional
    public void removeFavorite(User user, UUID bookId) {
        favoritesRepository.deleteByUserAndBookId(user, bookId);
    }
}