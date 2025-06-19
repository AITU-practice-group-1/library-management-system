package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
