package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Entities.Book;

import java.util.List;
import java.util.UUID;

public interface FavoriteBookService {


    void addBookToFavorites(UUID userId, UUID bookId);


    void removeBookFromFavorites(UUID userId, UUID bookId);

    List<Book> getFavoriteBooksForUser(UUID userId);

    boolean isBookInFavorites(UUID userId, UUID bookId);
}
