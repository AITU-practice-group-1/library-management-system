package com.example.librarymanagementsystem.Entities;

import lombok.Data;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan")
@Data
public class Loan {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="book_id",nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by", nullable = false)
    private User issuedBy; //librarian

    @CreationTimestamp
    @Column(name = "issue_date", updatable = false, nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "due_date",nullable = false)
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum LoanStatus {
        BORROWED,
        RETURNED,
        EXPIRED
        OVERDUE // Optional: Can be useful for filtering
    }

    /**
     * Helper method to check if a loan is past its due date and not yet returned.
     * @return true if the loan is overdue, false otherwise.
     */
    @Transient // This means it won't be persisted to the database
    public boolean isOverdue() {
        return this.status == LoanStatus.BORROWED && LocalDateTime.now().isAfter(this.dueDate);
    }
}