package com.example.librarymanagementsystem.mapper;

import com.example.librarymanagementsystem.DTOs.LoanResponseDTO;
import com.example.librarymanagementsystem.Entities.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel="spring", uses = {UserMapper.class})
public interface LoanMapper{
    LoanMapper INSTANCE = Mappers.getMapper(LoanMapper.class);
  
    LoanResponseDTO toResponseDto(Loan loan);
    @Mapping(target = "bookAuthor", source = "book.author")
    LoanDTO toDto (Loan loan);
    Loan toEntity(LoanDTO dto);
}