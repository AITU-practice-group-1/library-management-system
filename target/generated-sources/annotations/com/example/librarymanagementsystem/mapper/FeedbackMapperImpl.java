package com.example.librarymanagementsystem.mapper;

import com.example.librarymanagementsystem.DTOs.feedback.FeedbackRequestDTO;
import com.example.librarymanagementsystem.DTOs.feedback.FeedbackResponseDTO;
import com.example.librarymanagementsystem.Entities.Book;
import com.example.librarymanagementsystem.Entities.Feedback;
import com.example.librarymanagementsystem.Entities.User;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-12T15:31:39+0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
@Component
public class FeedbackMapperImpl implements FeedbackMapper {

    @Override
    public Feedback toEntity(FeedbackRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Feedback feedback = new Feedback();

        feedback.setUser( feedbackRequestDTOToUser( dto ) );
        feedback.setBook( feedbackRequestDTOToBook( dto ) );
        feedback.setRating( dto.getRating() );
        feedback.setComment( dto.getComment() );

        return feedback;
    }

    @Override
    public FeedbackResponseDTO toDTO(Feedback entity) {
        if ( entity == null ) {
            return null;
        }

        FeedbackResponseDTO feedbackResponseDTO = new FeedbackResponseDTO();

        feedbackResponseDTO.setUserId( entityUserId( entity ) );
        feedbackResponseDTO.setBookId( entityBookId( entity ) );
        feedbackResponseDTO.setFirstName( entityUserFirstName( entity ) );
        feedbackResponseDTO.setId( entity.getId() );
        feedbackResponseDTO.setRating( entity.getRating() );
        feedbackResponseDTO.setComment( entity.getComment() );
        feedbackResponseDTO.setCreatedAt( entity.getCreatedAt() );
        feedbackResponseDTO.setUpdatedAt( entity.getUpdatedAt() );

        return feedbackResponseDTO;
    }

    protected User feedbackRequestDTOToUser(FeedbackRequestDTO feedbackRequestDTO) {
        if ( feedbackRequestDTO == null ) {
            return null;
        }

        User user = new User();

        user.setId( feedbackRequestDTO.getUserId() );

        return user;
    }

    protected Book feedbackRequestDTOToBook(FeedbackRequestDTO feedbackRequestDTO) {
        if ( feedbackRequestDTO == null ) {
            return null;
        }

        Book book = new Book();

        book.setId( feedbackRequestDTO.getBookId() );

        return book;
    }

    private UUID entityUserId(Feedback feedback) {
        User user = feedback.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private UUID entityBookId(Feedback feedback) {
        Book book = feedback.getBook();
        if ( book == null ) {
            return null;
        }
        return book.getId();
    }

    private String entityUserFirstName(Feedback feedback) {
        User user = feedback.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getFirstName();
    }
}
