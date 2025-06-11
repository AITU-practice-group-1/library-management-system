package com.example.librarymanagementsystem.mapper;

import com.example.librarymanagementsystem.Entities.Feedback;
import com.example.librarymanagementsystem.DTOs.feedback.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "book.id", source = "bookId")
    Feedback toEntity(FeedbackRequestDTO dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookId", source = "book.id")
    FeedbackResponseDTO toDTO(Feedback entity);
}
