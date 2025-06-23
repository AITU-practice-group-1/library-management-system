package com.example.librarymanagementsystem.DTOs.book;


import com.example.librarymanagementsystem.DTOs.feedback.FeedbackResponseDTO;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.Optional;

@Data
@Builder
@Getter
@Setter
public class BookDetailViewDTO {
    private BookResponseDTO book;
    private boolean isFavorite;
    private boolean hasUserReviewed;
    private Page<FeedbackResponseDTO> reviews;
    private Optional<FeedbackResponseDTO> userReview;


}