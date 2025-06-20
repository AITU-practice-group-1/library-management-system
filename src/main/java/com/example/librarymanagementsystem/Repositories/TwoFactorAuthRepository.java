package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.TwoFactorAuthData;
import com.example.librarymanagementsystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;

import java.util.Optional;
import java.util.UUID;

public interface TwoFactorAuthRepository extends JpaRepository <TwoFactorAuthData, UUID>{
    Optional<TwoFactorAuthData> findByUser (User user);
}
