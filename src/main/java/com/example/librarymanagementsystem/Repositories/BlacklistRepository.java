package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.BlacklistEntry;
import com.example.librarymanagementsystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BlacklistRepository extends JpaRepository<BlacklistEntry, UUID> {

    List<BlacklistEntry> findByUserAndResolvedFalse(User user);
    List<BlacklistEntry> findAllByResolvedFalse();
    List<BlacklistEntry> findAllByUser(User user);

    // Новые фильтры:
    List<BlacklistEntry> findAllByAddedAtAfter(LocalDateTime date);
    List<BlacklistEntry> findAllByDaysOverdueGreaterThan(long days);
}
