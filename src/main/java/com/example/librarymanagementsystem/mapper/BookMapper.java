package com.example.librarymanagementsystem.mapper;

import com.example.librarymanagementsystem.DTOs.book.BookCreateDTO;
import com.example.librarymanagementsystem.DTOs.book.BookResponseDTO;
import com.example.librarymanagementsystem.DTOs.book.BookUpdateDTO;
import com.example.librarymanagementsystem.Entities.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    // DTO to Entity
    Book toEntity(BookCreateDTO dto);

    // Entity to DTO
    BookResponseDTO toResponseDTO(Book book);

    // For updates: copies DTO fields to an existing entity
    @Mapping(target = "id", ignore = true) // Don't copy the ID
    void updateEntityFromDto(BookUpdateDTO dto, @MappingTarget Book book);
}