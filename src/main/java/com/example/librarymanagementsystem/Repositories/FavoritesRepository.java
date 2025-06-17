package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.Favorites;
import com.example.librarymanagementsystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FavoritesRepository extends JpaRepository<Favorites, UUID> {
    List<Favorites> findByUser(User user);
    void deleteByUserAndBookId(User user, UUID bookId);
}