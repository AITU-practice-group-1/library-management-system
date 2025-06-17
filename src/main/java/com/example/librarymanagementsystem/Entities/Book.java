package com.example.librarymanagementsystem.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(columnNames = "isbn", name = "uk_book_isbn")
})
@Data
@ToString
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "rating_sum", nullable = false)
    private long ratingSum = 0L;

    @Column(name = "rating_count", nullable = false)
    private long ratingCount = 0L;

    @Column(name = "rating_average", nullable = false, precision = 3, scale = 2)
    private BigDecimal ratingAverage = BigDecimal.ZERO;

    // --- Business Logic Encapsulation ---

    /**
     * Adds a new rating and recalculates the average.
     * @param rating The rating value (1-5).
     */
    public void addRating(int rating) {
        validateRating(rating);
        this.ratingSum += rating;
        this.ratingCount++;
        recalculateAverage();
    }

    /**
     * Removes a rating and recalculates the average.
     * @param ratingToRemove The rating value to remove (1-5).
     */
    public void removeRating(int ratingToRemove) {
        validateRating(ratingToRemove);
        if (this.ratingCount > 0 && this.ratingSum >= ratingToRemove) {
            this.ratingSum -= ratingToRemove;
            this.ratingCount--;
            recalculateAverage();
        }
    }

    /**
     * Updates an existing rating and recalculates the average.
     * @param oldRating The user's previous rating.
     * @param newRating The user's new rating.
     */
    public void updateRating(int oldRating, int newRating) {
        validateRating(oldRating);
        validateRating(newRating);
        this.ratingSum = (this.ratingSum - oldRating) + newRating;
        // The rating count remains the same, so we just recalculate the average.
        recalculateAverage();
    }

    private void recalculateAverage() {
        if (this.ratingCount > 0) {
            this.ratingAverage = BigDecimal.valueOf(this.ratingSum)
                    .divide(BigDecimal.valueOf(this.ratingCount), 2, RoundingMode.HALF_UP);
        } else {
            this.ratingAverage = BigDecimal.ZERO;
            this.ratingSum = 0L; // Ensure sum is also zero if count is zero
        }
    }

    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}