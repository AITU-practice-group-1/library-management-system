package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.Feedback;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    List<Feedback> findByUserId(UUID userId, Pageable pageable);

    List<Feedback> findByBookId(UUID bookId, Pageable pageable);
}
