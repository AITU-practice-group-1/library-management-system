package com.example.librarymanagementsystem.security;

import com.example.librarymanagementsystem.Entities.Feedback;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.FeedbackRepository;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("feedbackSecurityService")
@RequiredArgsConstructor
public class FeedbackSecurityService {
    private final FeedbackRepository feedbackRepo;
    private final UserRepository userRepo;

    public boolean isOwner(UUID feedbackId, User userDetails) {
        System.out.println("^^ " + userDetails.getUsername() + " " + userDetails.getId() + " " + userDetails.getEmail());
        System.out.println(userDetails == null);
        if (userDetails == null) return false;

        Feedback feedback1 = feedbackRepo.findById(feedbackId).get();
        System.out.println("^^ " + feedback1.getUser().getUsername() + " " + feedback1.getUser().getId());

        Boolean b = feedbackRepo.findById(feedbackId)
                .map(feedback -> feedback.getUser().getId().equals(userDetails.getId()))
                .orElse(false);

        return b;
    }
}
