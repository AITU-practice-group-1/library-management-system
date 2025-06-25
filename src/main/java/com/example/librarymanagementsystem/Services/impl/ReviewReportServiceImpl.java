package com.example.librarymanagementsystem.Services.impl;

import com.example.librarymanagementsystem.Entities.Feedback;
import com.example.librarymanagementsystem.Entities.ReviewReport;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.FeedbackRepository;
import com.example.librarymanagementsystem.Repositories.ReviewReportRepository;
import com.example.librarymanagementsystem.Services.BlacklistService;
import com.example.librarymanagementsystem.Services.BookService;
import com.example.librarymanagementsystem.Services.FeedbackService;
import com.example.librarymanagementsystem.Services.ReviewReportService;
import com.example.librarymanagementsystem.util.ReviewReportStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.JsonUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewReportServiceImpl implements ReviewReportService {
    private final ReviewReportRepository reviewReportRepository;
    private final FeedbackRepository feedbackRepository;
    private final BookService bookService;
    private final BlacklistService blacklistService;

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewReport> getAllReviewReport(Pageable pageable) {
        return reviewReportRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewReport> getAllReviewReports(ReviewReportStatus status, Pageable pageable) {
        return reviewReportRepository.findAllByStatus(status, pageable);
    }


    @Override
    public ReviewReport createReviewReport(UUID feedbackId, User reportingUser) {
        Feedback feedbackToReport = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found" + feedbackId));
        if (reviewReportRepository.existsByFeedbackAndUser(feedbackToReport, reportingUser)) {
            throw new IllegalArgumentException("You have already reported this review");
        }

        ReviewReport newReport = new ReviewReport();
        newReport.setFeedback(feedbackToReport);
        newReport.setUser(reportingUser);
        newReport.setStatus(ReviewReportStatus.PENDING);

        return reviewReportRepository.save(newReport);
    }

    @Override
    @Transactional
    public void approveReviewReport(UUID reportId) {
        ReviewReport approvedReport = reviewReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("ReviewReport not found with id: " + reportId));

        Feedback feedbackToDelete = approvedReport.getFeedback();
        String reason = "Violation of ethical rules";
        blacklistService.handleViolation(approvedReport.getUser(), reason);
        if (feedbackToDelete == null) {
            reviewReportRepository.delete(approvedReport);
            return;
        }
        int ratingToRemove = feedbackToDelete.getRating();
        UUID bookId = feedbackToDelete.getBook().getId();

        reviewReportRepository.deleteAllByFeedback(feedbackToDelete);

        feedbackRepository.delete(feedbackToDelete);
        bookService.recalculateBookRatingOnDelete(bookId, ratingToRemove);

    }


    @Override
    public void rejectReviewReport(UUID id) {
        ReviewReport report = reviewReportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review report not found"));
        report.setStatus(ReviewReportStatus.REJECTED);
        reviewReportRepository.save(report);
    }
}
