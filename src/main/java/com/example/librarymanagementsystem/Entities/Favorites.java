package com.example.librarymanagementsystem.Entities;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "favoritebooks")
@Data
public class Favorites {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name ="book_id", nullable = false)
    private Book book;

    public void setUser(User user) {
        this.user = user;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}