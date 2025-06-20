package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.BlacklistEntry;
import com.example.librarymanagementsystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlacklistRepository extends JpaRepository<BlacklistEntry, java.util.UUID> {

    List<BlacklistEntry> findByUserAndResolvedFalse(User user);
    List<BlacklistEntry> findAllByResolvedFalse();
    List<BlacklistEntry> findAllByUser(User user);
    List<BlacklistEntry> findAllByByDueDate();
}
