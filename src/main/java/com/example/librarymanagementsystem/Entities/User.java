package com.example.librarymanagementsystem.Entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, name="first_name")
    private String firstName;
    @Column(nullable = false, name="last_name")
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false, name = "is_active")
    private boolean isActive;
    @Column(name = "is_banned")
    private boolean isBanned;
    @Column(name = "created_at",  updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "image_id")
    private String imageId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole().toUpperCase()));
    }

    @Override
    public String getUsername() {
        return email;
    }

}