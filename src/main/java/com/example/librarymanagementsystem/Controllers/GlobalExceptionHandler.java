package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.exceptions.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.nio.file.AccessDeniedException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred: " + ex.getMessage());
        return "error";
    }

    @ExceptionHandler(FeedbackNotFoundException.class)
    public String handleFeedbackNotFound(FeedbackNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(FeedbackAlreadyExistsException.class)
    public String handleFeedbackAlreadyExists(FeedbackNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(FeedbackNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }


    @ExceptionHandler(BookNotFoundException.class)
    public String handleBookNotFoundException(BookNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public String handleReservationNotFoundException(ReservationNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(NotEnoughBooksException.class)
    public String handleNotEnoughBooks(NotEnoughBooksException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
}
