package com.example.librarymanagementsystem.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "two_factor_auth")
public class TwoFactorAuthData {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String secret;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;
}
