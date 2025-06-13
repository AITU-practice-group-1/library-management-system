package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    Page<Feedback> findByUserId(UUID userId, Pageable pageable);

    Page<Feedback> findByBookId(UUID bookId, Pageable pageable);

    Optional<Feedback> findByBookIdAndUserId(UUID bookId, UUID userId);

    boolean existsByBookIdAndUserId(UUID bookId, UUID userId);
}
