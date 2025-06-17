package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.feedback.FeedbackRequestDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackResponseDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackUpdateDTO;
import com.example.librarymanagementsystem.Entities.Feedback;
import com.example.librarymanagementsystem.Repositories.FeedbackRepository;
import com.example.librarymanagementsystem.mapper.FeedbackMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FeedbackService {
    private FeedbackRepository feedbackRepo;
    private FeedbackMapper feedbackMapper;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepo, FeedbackMapper feedbackMapper) {
        this.feedbackRepo = feedbackRepo;
        this.feedbackMapper = feedbackMapper;
    }

    public FeedbackResponseDTO getById(UUID id) {
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));
        return feedbackMapper.toDTO(feedback);
    }

    public Page<FeedbackResponseDTO> getByBookId(UUID bookId, Pageable pageable) throws Exception{
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

    @Transactional()
    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO feedbackRequestDTO) {
        if (feedbackRepo.existsByBookIdAndUserId(feedbackRequestDTO.getBookId(), feedbackRequestDTO.getUserId())) {
            throw new IllegalStateException("You already have a feedback");
        }
        Feedback feedback = feedbackMapper.toEntity(feedbackRequestDTO);

        feedbackRepo.save(feedback);

        return feedbackMapper.toDTO(feedback);
    }

    @Transactional()
    @PreAuthorize("@feedbackService.isFeedbackOwner(#id, principal)")
    public FeedbackResponseDTO updateFeedback(UUID id, FeedbackUpdateDTO feedbackUpdateDTO) {
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));

        feedback.setRating(feedbackUpdateDTO.getRating());
        feedback.setComment(feedbackUpdateDTO.getComment());

        Feedback feedbackUpdated = feedbackRepo.save(feedback);
        return feedbackMapper.toDTO(feedbackUpdated);
    }

    @Transactional()
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or @feedbackService.isFeedbackOwner(#id, principal)")
    public void deleteFeedback(UUID id) {
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with ID: " + id));
        feedbackRepo.delete(feedback);
    }

    public boolean isFeedbackOwner(UUID feedbackId, UserDetails userDetails) {
        if (userDetails == null) return false;
        Feedback feedback = feedbackRepo.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));
        return feedback.getUser().getEmail().equals((userDetails.getUsername()));
    }
}
