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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FeedbackService {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

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
        logger.info("Fetching feedback with ID: {}", id);
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Feedback not found with ID: {}", id);
                    return new FeedbackNotFoundException("Feedback not found with ID:" + id);
                });
        return feedbackMapper.toDTO(feedback);
    }

    public Page<FeedbackResponseDTO> getByBookId(UUID bookId, Pageable pageable) {
        logger.info("Fetching feedback for book ID: {}", bookId);
        Page<Feedback> feedbackPage = feedbackRepo.findByBookId(bookId, pageable);
        return feedbackPage.map(feedbackMapper::toDTO);
    }

    public Page<FeedbackResponseDTO> getByUserId(UUID userId, Pageable pageable) {
        logger.info("Fetching feedback for user ID: {}", userId);
        Page<Feedback> feedbackPage = feedbackRepo.findByUserId(userId, pageable);
        return feedbackPage.map(feedbackMapper::toDTO);
    }

    public Optional<FeedbackResponseDTO> getByBookIdAndUserId(UUID bookId, UUID userId) {
        logger.info("Fetching feedback for book ID: {} and user ID: {}", bookId, userId);
        return feedbackRepo.findByBookIdAndUserId(bookId, userId)
                .map(feedbackMapper::toDTO);
    }

    @Transactional
    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO feedbackRequestDTO, User user) {
        logger.info("Creating feedback for book ID: {} by user ID: {}", feedbackRequestDTO.getBookId(), user.getId());

        if (feedbackRepo.existsByBookIdAndUserId(feedbackRequestDTO.getBookId(), feedbackRequestDTO.getUserId())) {
            logger.error("Feedback already exists for book ID: {} by user ID: {}", feedbackRequestDTO.getBookId(), feedbackRequestDTO.getUserId());
            throw new FeedbackAlreadyExistsException("You have already created feedback for this book");
        }

        Feedback feedback = feedbackMapper.toEntity(feedbackRequestDTO);
        feedback.setUser(user);
        feedbackRepo.save(feedback);

        bookService.updateBookRating(feedbackRequestDTO.getBookId(), feedbackRequestDTO.getRating());
        logger.info("Feedback created successfully for book ID: {}", feedbackRequestDTO.getBookId());

        return feedbackMapper.toDTO(feedback);
    }

    @Transactional
    @PreAuthorize("@feedbackSecurityService.isOwner(#id, principal)")
    public FeedbackResponseDTO updateFeedback(UUID id, FeedbackUpdateDTO feedbackUpdateDTO) {
        logger.info("Updating feedback with ID: {}", id);
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cannot update. Feedback not found with ID: {}", id);
                    return new FeedbackNotFoundException("Cannot update. Feedback not found with ID: " + id);
                });
        int oldRating = feedback.getRating();

        feedback.setRating(feedbackUpdateDTO.getRating());
        feedback.setComment(feedbackUpdateDTO.getComment());
        Feedback updatedFeedback = feedbackRepo.save(feedback);

        bookService.recalculateBookRatingOnUpdate(
                feedback.getBook().getId(),
                oldRating,
                feedbackUpdateDTO.getRating()
        );

        logger.info("Feedback with ID: {} updated successfully", id);
        return feedbackMapper.toDTO(updatedFeedback);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @feedbackSecurityService.isOwner(#id, principal)")
    public void deleteFeedback(UUID id) {
        logger.info("Deleting feedback with ID: {}", id);
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cannot delete. Feedback not found with ID: {}", id);
                    return new FeedbackNotFoundException("Cannot delete. Feedback not found with ID:" + id);
                });

        bookService.recalculateBookRatingOnDelete(feedback.getBook().getId(), feedback.getRating());
        feedbackRepo.delete(feedback);
        logger.info("Feedback with ID: {} deleted successfully", id);
    }
}
