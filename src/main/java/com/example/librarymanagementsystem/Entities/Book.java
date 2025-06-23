package com.example.librarymanagementsystem.Entities;

import com.example.librarymanagementsystem.util.Genre;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(columnNames = "isbn", name = "uk_book_isbn")
})
@Data
@ToString(exclude = {"feedbacks", "favoriteBooks"}) // Exclude collections from toString to avoid performance issues
@EqualsAndHashCode(exclude = {"feedbacks", "favoriteBooks"}) // Also exclude from equals/hashCode
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @Column(nullable = false)
    @NotEmpty(message = "Author cannot be empty")
    private String author;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "ISBN cannot be empty")
    @Pattern(regexp = "\\d{13}", message = "ISBN must be a 13-digit number")
    private String isbn;

    @Column(nullable = false)
    @NotEmpty(message = "Publisher cannot be empty")
    private String publisher;

    @Column(name = "published_year", nullable = false)
    @Max(value = 2025, message = "Published year cannot be in the future") // Consider making this dynamic
    private int publishedYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Genre must be selected")
    private Genre genre;

    @Column(nullable = false, length = 1000)
    @NotEmpty(message = "Description cannot be empty")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Column(name = "total_copies", nullable = false)
    @Min(value = 1, message = "Total copies must be at least 1")
    private int totalCopies;

    @Column(name = "available_copies", nullable = false)
    @Min(value = 0, message = "Available copies cannot be negative")
    private int availableCopies;

    // --- Relationships ---
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FavoriteBook> favoriteBooks = new ArrayList<>();


    // --- Rating Fields ---
    @Column(name = "rating_sum", nullable = false)
    private long ratingSum = 0L;

    @Column(name = "rating_count", nullable = false)
    private long ratingCount = 0L;

    @Column(name = "rating_average", nullable = false, precision = 3, scale = 2)
    private BigDecimal ratingAverage = BigDecimal.ZERO;

    // --- Timestamps ---
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "image_id")
    private String imageId;

    // --- Business Logic for Ratings ---
    public void addRating(int rating) {
        validateRating(rating);
        this.ratingSum += rating;
        this.ratingCount++;
        recalculateAverage();
    }

    public void removeRating(int ratingToRemove) {
        validateRating(ratingToRemove);
        if (this.ratingCount > 0 && this.ratingSum >= ratingToRemove) {
            this.ratingSum -= ratingToRemove;
            this.ratingCount--;
            recalculateAverage();
        }
    }

    public void updateRating(int oldRating, int newRating) {
        validateRating(oldRating);
        validateRating(newRating);
        this.ratingSum = (this.ratingSum - oldRating) + newRating;
        recalculateAverage();
    }

    private void recalculateAverage() {
        if (this.ratingCount > 0) {
            this.ratingAverage = BigDecimal.valueOf(this.ratingSum)
                    .divide(BigDecimal.valueOf(this.ratingCount), 2, RoundingMode.HALF_UP);
        } else {
            this.ratingAverage = BigDecimal.ZERO;
            this.ratingSum = 0L;
        }
    }

    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}