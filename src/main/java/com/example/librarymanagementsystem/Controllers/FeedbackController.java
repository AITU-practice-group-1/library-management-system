package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackRequestDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackResponseDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackUpdateDTO;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.FeedbackService;
import com.example.librarymanagementsystem.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/reviews")
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
        Page<FeedbackResponseDTO> feedbacksPage = null;
        try{
             feedbacksPage = feedbackService.getByBookId(bookId, pageable);
        }catch (Exception e){
            System.out.println("CANNOT GET FEEDBACKS page " + e.getMessage());
        }

        UUID currentUserId = null;
        boolean isAdmin = false;
        try {
            UserDTO currentUser = userService.getAuhtenticatedUser();
            currentUserId = currentUser.getId();
            isAdmin = currentUser.getRole().equals("ROLE_ADMIN");
        } catch (Exception e) {
            System.out.println("NOT AUTHORIZED: " + e.getMessage());
        }
        model.addAttribute("feedbacks", feedbacksPage);
        model.addAttribute("bookId", bookId);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("isAdmin", isAdmin);
        return "feedback/list";
    }

    // create feedback to specific book
    @PostMapping("/add")
    public String createFeedback(@Valid @ModelAttribute("feedbackRequest") FeedbackRequestDTO feedbackRequestDTO,
                                 BindingResult result,
                                 @AuthenticationPrincipal UserDTO user,
                                 RedirectAttributes redirectAttributes) {
        System.out.println(user);
        try {
            user = userService.getAuhtenticatedUser();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.feedbackRequest", result);
            redirectAttributes.addFlashAttribute("feedbackRequest", feedbackRequestDTO);
            return "redirect:/books/" + feedbackRequestDTO.getBookId() + "?error=true";
        }

        try {
            feedbackRequestDTO.setUserId(user.getId());
            feedbackService.createFeedback(feedbackRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Your review has been submitted successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred.");
        }

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
        redirectAttributes.addFlashAttribute("successMessage", "Your review has been updated.");
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
    public String deleteFeedback(@PathVariable UUID id, @AuthenticationPrincipal UserDTO user, RedirectAttributes redirectAttributes) {
        FeedbackResponseDTO feedback = feedbackService.getById(id);

        if (!feedback.getUserId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to delete this review.");
            return "redirect:/books/" + feedback.getBookId();
        }

        feedbackService.deleteFeedback(id);
        redirectAttributes.addFlashAttribute("successMessage", "The review has been deleted.");
        return "redirect:/books/" + feedback.getBookId();
    }
}