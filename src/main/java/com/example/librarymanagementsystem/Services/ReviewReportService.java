package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Entities.ReviewReport;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.util.ReviewReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReviewReportService {
    Page<ReviewReport> getAllReviewReport(Pageable pageable);
    ReviewReport createReviewReport(UUID feedbackId, User reportingUser);
    void approveReviewReport(UUID id);
    void rejectReviewReport(UUID id);
    Page<ReviewReport> getAllReviewReports(ReviewReportStatus status, Pageable pageable);

}
