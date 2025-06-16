package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.feedback.FeedbackRequestDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackResponseDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackUpdateDTO;
import com.example.librarymanagementsystem.Entities.Feedback;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.FeedbackRepository;
import com.example.librarymanagementsystem.exceptions.FeedbackAlreadyExistsException;
import com.example.librarymanagementsystem.exceptions.FeedbackNotFoundException;
import com.example.librarymanagementsystem.mapper.FeedbackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FeedbackService {
    private FeedbackRepository feedbackRepo;
    private FeedbackMapper feedbackMapper;
    private BookService bookService;
    private UserServices userService;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepo, FeedbackMapper feedbackMapper, BookService bookService, UserServices userService) {
        this.feedbackRepo = feedbackRepo;
        this.feedbackMapper = feedbackMapper;
        this.bookService = bookService;
        this.userService = userService;
    }

    public FeedbackResponseDTO getById(UUID id) {
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback not found with ID:" + id));
        return feedbackMapper.toDTO(feedback);
    }

    public Page<FeedbackResponseDTO> getByBookId(UUID bookId, Pageable pageable) {
            Page<Feedback> feedbackPage = feedbackRepo.findByBookId(bookId, pageable);

            return feedbackPage.map(feedbackMapper::toDTO);
    }

    public Page<FeedbackResponseDTO> getByUserId(UUID userId, Pageable pageable) {
        Page<Feedback> feedbackPage = feedbackRepo.findByUserId(userId, pageable);
        return feedbackPage.map(feedbackMapper::toDTO);
    }

    public Optional<FeedbackResponseDTO> getByBookIdAndUserId(UUID bookId, UUID userId) {
        return feedbackRepo.findByBookIdAndUserId(bookId, userId)
                .map(feedbackMapper::toDTO);
    }

    @Transactional
    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO feedbackRequestDTO, User user) {

        if (feedbackRepo.existsByBookIdAndUserId(feedbackRequestDTO.getBookId(), feedbackRequestDTO.getUserId())) {
            throw new FeedbackAlreadyExistsException("You have already created feedback for this book");
        }


        Feedback feedback = feedbackMapper.toEntity(feedbackRequestDTO);
        feedback.setUser(user);
        feedbackRepo.save(feedback);

        bookService.updateBookRating(feedbackRequestDTO.getBookId(), feedbackRequestDTO.getRating());

        return feedbackMapper.toDTO(feedback);
    }

    @Transactional()
    @PreAuthorize("@feedbackSecurityService.isOwner(#id, principal)")
    public FeedbackResponseDTO updateFeedback(UUID id, FeedbackUpdateDTO feedbackUpdateDTO) {
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException("Cannot update. Feedback not found with ID:" + id));

        int oldRating = feedbackUpdateDTO.getRating();
        feedback.setRating(feedbackUpdateDTO.getRating());
        feedback.setComment(feedbackUpdateDTO.getComment());
        Feedback updatedFeedback = feedbackRepo.save(feedback);

        bookService.recalculateBookRatingOnUpdate(feedback.getBook().getId(), oldRating, feedbackUpdateDTO.getRating());

        return feedbackMapper.toDTO(updatedFeedback);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @feedbackSecurityService.isOwner(#id, principal)")
    public void deleteFeedback(UUID id) {
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException("Cannot delete. Feedback not found with ID:" + id));

        bookService.recalculateBookRatingOnDelete(feedback.getBook().getId(), feedback.getRating());
        feedbackRepo.delete(feedback);
    }
}
