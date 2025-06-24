package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.BlacklistEntry;
import com.example.librarymanagementsystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BlacklistRepository extends JpaRepository<BlacklistEntry, UUID> {

    List<BlacklistEntry> findByIsBanTrueAndResolvedFalse();
    List<BlacklistEntry> findByIsBanFalseAndResolvedFalse();
    List<BlacklistEntry> findByUserOrderByAddedAtDesc(User user);
    List<BlacklistEntry> findByUserAndIsBanFalseAndResolvedFalse(User user);
    List<BlacklistEntry> findByUserAndIsBanTrueAndResolvedFalse(User user);
    List<BlacklistEntry> findByIsBanFalseAndResolvedFalseAndAddedAtBefore(LocalDateTime cutoff);
}
