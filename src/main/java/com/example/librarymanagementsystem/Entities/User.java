package com.example.librarymanagementsystem.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, name="first_name")
    private String firstName;
    @Column(nullable = false, name="last_name")
    private String lastName;
    @Column(nullable = false, unique = true)
    public String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false, name = "is_active")
    private boolean isActive;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public String UserEmail = email;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}