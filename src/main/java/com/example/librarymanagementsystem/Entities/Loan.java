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

    @ManyToOne
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name ="book_id",nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "issued_by", nullable = false)
    private User issuedBy; //librarian

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum LoanStatus {
        BORROWED,
        RETURNED
    }

}
