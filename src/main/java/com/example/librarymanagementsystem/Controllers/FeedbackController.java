package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackRequestDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackResponseDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackUpdateDTO;
import com.example.librarymanagementsystem.Services.FeedbackService;
import com.example.librarymanagementsystem.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserServices userService;


    @Autowired
    public FeedbackController(FeedbackService feedbackService, UserServices userService) {
        this.feedbackService = feedbackService;
        this.userService = userService;
    }

    // all feedbacks of book
    @GetMapping("/book/{bookId}")
    public String getFeedbacksByBook(@PathVariable UUID bookId, Model model,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<FeedbackResponseDTO> feedbacksPage = feedbackService.getByBookId(bookId, pageable);
        UUID currentUserId = null;
        boolean isAdmin = false;
        try {
            UserDTO currentUser = userService.getAuhtenticatedUser();
            currentUserId = currentUser.getId();
            isAdmin = currentUser.getRole().equals("ROLE_ADMIN");
        } catch (Exception ignored) {

        }
        model.addAttribute("feedbacks", feedbacksPage); // Pass the Page object
        model.addAttribute("bookId", bookId);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("isAdmin", isAdmin);
        return "feedback/list";
    }

    // create feedback to specific book
    @PostMapping("/book/{bookId}")
    public String createFeedback(@PathVariable UUID bookId, @Valid @ModelAttribute FeedbackRequestDTO feedbackRequestDTO,
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("bookId", bookId);
            return "feedback/create";
        }

        UUID userId = null; // Retrieve userId from UserService
        try {
            userId = userService.getAuhtenticatedUser().getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        feedbackRequestDTO.setUserId(userId);
        feedbackRequestDTO.setBookId(bookId);

        FeedbackResponseDTO feedback = feedbackService.createFeedback(feedbackRequestDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback created successfully!");
        return "redirect:/api/feedback/book/" + bookId;
    }

    // create feedback page with form
    @GetMapping("/book/{bookId}/create")
    public String showCreateForm(@PathVariable UUID bookId, Model model) {
        FeedbackRequestDTO feedbackRequestDTO = new FeedbackRequestDTO();
        feedbackRequestDTO.setBookId(bookId);
        model.addAttribute("feedbackRequestDTO", feedbackRequestDTO);
        model.addAttribute("bookId", bookId);
        return "feedback/create";
    }

    // update specific feedback (only user's personal feedbacks)
    @PostMapping("/{id}/edit")
    public String updateFeedback(@PathVariable UUID id, @Valid @ModelAttribute FeedbackUpdateDTO feedbackUpdateDTO,
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("feedbackId", id);
            return "feedback/update";
        }

        UserDTO currentUser = null;
        FeedbackResponseDTO feedback = feedbackService.getById(id);
        try {
            currentUser = userService.getAuhtenticatedUser();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!currentUser.getId().equals(feedback.getUserId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to delete this feedback!");
            return "redirect:/api/feedback/book/" + feedback.getBookId();
        }

        FeedbackResponseDTO updatedFeedback = feedbackService.updateFeedback(id, feedbackUpdateDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback updated successfully!");
        return "redirect:/api/feedback/book/" + updatedFeedback.getBookId();
    }

    // update page with form
    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable UUID id, Model model) {
        FeedbackResponseDTO feedback = feedbackService.getById(id);
        model.addAttribute("feedback", feedback);
        return "feedback/update";
    }

    // delete button (only user's personal feedbacks)
    @PostMapping("/{id}/delete")
    public String deleteFeedback(@PathVariable UUID id, RedirectAttributes redirectAttributes) {

        UserDTO currentUser = null;
        FeedbackResponseDTO feedback = feedbackService.getById(id);
        try {
            currentUser = userService.getAuhtenticatedUser();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(currentUser.getRole());
        if (!currentUser.getId().equals(feedback.getUserId()) && (!Objects.equals(currentUser.getRole(), "ROLE_ADMIN"))) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to delete this feedback!");
            return "redirect:/api/feedback/book/" + feedback.getBookId();
        }
        feedbackService.deleteFeedback(id);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback deleted successfully!");
        return "redirect:/api/feedback/book/" + feedback.getBookId();
    }
}