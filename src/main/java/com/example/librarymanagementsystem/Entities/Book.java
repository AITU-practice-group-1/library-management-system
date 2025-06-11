package com.example.librarymanagementsystem.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books")
@Data
public class Book {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private int published_year;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int total_copies;

    @Column(nullable = false)
    private int available_copies;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    //@OneToMany(mappedBy = "book")
    //private List<Feedback> feedbacks = new ArrayList<>();
}