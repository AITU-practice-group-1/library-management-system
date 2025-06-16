package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.Feedback;
import com.example.librarymanagementsystem.Entities.ReviewReport;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.util.ReviewReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, UUID> {

    boolean existsByFeedbackAndUser(Feedback feedback, User user);
    Page<ReviewReport> findAllByStatus(ReviewReportStatus status, Pageable pageable);
    void deleteAllByFeedback(Feedback feedback);
}
