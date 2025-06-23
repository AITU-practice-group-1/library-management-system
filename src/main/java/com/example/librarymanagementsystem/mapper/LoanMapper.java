package com.example.librarymanagementsystem.mapper;

import com.example.librarymanagementsystem.DTOs.loan.LoanDTO;
import com.example.librarymanagementsystem.DTOs.loan.LoanResponseDTO;
import com.example.librarymanagementsystem.Entities.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel="spring", uses = {UserMapper.class})
public interface LoanMapper{
    LoanMapper INSTANCE = Mappers.getMapper(LoanMapper.class);

    @Mapping(target = "bookAuthor", source = "book.author")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "bookIsbn", source = "book.isbn")
    LoanResponseDTO toResponseDto(Loan loan);
    @Mapping(target = "bookAuthor", source = "book.author")
    LoanDTO toDto (Loan loan);
    Loan toEntity(LoanDTO dto);
}