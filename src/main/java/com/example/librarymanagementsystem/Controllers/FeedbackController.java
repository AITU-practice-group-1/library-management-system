package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackRequestDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackResponseDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackUpdateDTO;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.BookService;
import com.example.librarymanagementsystem.Services.FeedbackService;
import com.example.librarymanagementsystem.Services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.util.UUID;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserServices userService;

    // all feedbacks of book
    @GetMapping("/book/{bookId}")
    public String getFeedbacksByBook(@PathVariable UUID bookId,
                                     Model model,
                                     Pageable pageable,
                                     @AuthenticationPrincipal User user) {
        Page<FeedbackResponseDTO> feedbacksPage = feedbackService.getByBookId(bookId, pageable);
        model.addAttribute("feedbacks", feedbacksPage);
        model.addAttribute("bookId", bookId);
//
//        UUID currentUserId = null;
//        boolean isAdmin = false;
//        try {
//            UserDTO currentUser = userService.getAuthenticatedUser();
//            currentUserId = currentUser.getId();
//            isAdmin = currentUser.getRole().equals("ROLE_ADMIN");
//        } catch (Exception e) {
//            System.out.println("NOT AUTHORIZED: " + e.getMessage());
//        }
//        model.addAttribute("feedbacks", feedbacksPage);
//        model.addAttribute("bookId", bookId);
//        model.addAttribute("currentUserId", currentUserId);
//        model.addAttribute("isAdmin", isAdmin);
        if (user != null) {
            model.addAttribute("user", user);
        }

        return "feedback/list";
    }

    // create feedback to specific book
    @PostMapping("/add")
    public String createFeedback(@Valid @ModelAttribute("feedbackRequest") FeedbackRequestDTO feedbackRequestDTO,
                                 BindingResult result,
                                 @AuthenticationPrincipal User user,
                                 RedirectAttributes redirectAttributes) {
//        System.out.println(user);
//        try {
//            user = userService.getAuthenticatedUser();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        if (result.hasErrors()) {
//            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.feedbackRequest", result);
//            redirectAttributes.addFlashAttribute("feedbackRequest", feedbackRequestDTO);
//            return "redirect:/books/" + feedbackRequestDTO.getBookId() + "?error=true";
//        }
//
//        try {
//            feedbackRequestDTO.setUserId(user.getId());
//            feedbackService.createFeedback(feedbackRequestDTO);
//            bookService.updateBookRating(feedbackRequestDTO.getBookId(), feedbackRequestDTO.getRating());
//            redirectAttributes.addFlashAttribute("successMessage", "Your review has been submitted successfully!");
//        } catch (IllegalStateException e) {
//            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred.");
//        }
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.feedbackRequest", result);
            redirectAttributes.addFlashAttribute("feedbackRequestDTO", feedbackRequestDTO);
            return "redirect:/books/" + feedbackRequestDTO.getBookId() + "?error=true";
        }

        feedbackService.createFeedback(feedbackRequestDTO, user);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback created successfully");
        return "redirect:/books/" + feedbackRequestDTO.getBookId();
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
    @PostMapping("/update")
    public String updateFeedback(@Valid @ModelAttribute("userReview") FeedbackUpdateDTO feedbackUpdateDTO,
                                 BindingResult result,
                                 @AuthenticationPrincipal User user,
                                 RedirectAttributes redirectAttributes) {

        FeedbackResponseDTO existingReview = feedbackService.getById(feedbackUpdateDTO.getId());

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userReview", result);
            redirectAttributes.addFlashAttribute("userReview", feedbackUpdateDTO);
            return "redirect:/books/" + existingReview.getBookId() + "?error=true&edit=true";
        }

        if (!existingReview.getUserId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to edit this review.");
            return "redirect:/books/" + existingReview.getBookId();
        }

        feedbackService.updateFeedback(feedbackUpdateDTO.getId(), feedbackUpdateDTO);

        redirectAttributes.addFlashAttribute("successMessage", "Your review has been updated successfully.");
        return "redirect:/books/" + existingReview.getBookId();
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
        UUID bookId = feedbackService.getById(id).getBookId();

        feedbackService.deleteFeedback(id);

        redirectAttributes.addFlashAttribute("successMessage", "Your review has been deleted successfully.");
        return "redirect:/books/" + bookId;
    }
}