package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Entities.ReviewReport;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.FeedbackService;
import com.example.librarymanagementsystem.Services.ReviewReportService;
import com.example.librarymanagementsystem.util.ReviewReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/review-reports")
public class ReviewReportController {

    private final ReviewReportService reviewReportService;
    private final FeedbackService feedbackService;

    @Autowired
    public ReviewReportController(ReviewReportService reviewReportService, FeedbackService feedbackService) {
        this.reviewReportService = reviewReportService;
        this.feedbackService = feedbackService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String getAllReviewReports(Model model,
                                      @RequestParam(name = "status", defaultValue = "PENDING") String statusString,
                                      @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        ReviewReportStatus status;
        try {
            status = ReviewReportStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            status = ReviewReportStatus.PENDING;
        }

        Page<ReviewReport> reviewReports = reviewReportService.getAllReviewReports(status, pageable);

        model.addAttribute("reviewReports", reviewReports);
        model.addAttribute("currentStatus", status.name());

        return "admin/reviewReports";
    }

    @PostMapping("/report/{feedbackId}")
    public String createReviewReport (@PathVariable UUID feedbackId,
                                      @AuthenticationPrincipal User currentUser,
                                      RedirectAttributes redirectAttributes) {
        try {
            reviewReportService.createReviewReport(feedbackId, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Review reported successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        UUID bookId = feedbackService.getById(feedbackId).getBookId();
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveReviewReport(@PathVariable("id") UUID id, RedirectAttributes redirectAttributes) {
        reviewReportService.approveReviewReport(id);
        redirectAttributes.addFlashAttribute("successMessage", "Report approved and review has been deleted.");
        return "redirect:/review-reports";
    }


    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectReviewReport(@PathVariable("id") UUID id, RedirectAttributes redirectAttributes) {
        reviewReportService.rejectReviewReport(id);
        redirectAttributes.addFlashAttribute("successMessage", "Report has been rejected.");

        return "redirect:/review-reports";
    }
}
