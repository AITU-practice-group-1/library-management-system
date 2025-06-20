package com.example.librarymanagementsystem.mapper;

import com.example.librarymanagementsystem.DTOs.LoanResponseDTO;
import com.example.librarymanagementsystem.Entities.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LoanMapper {
    LoanMapper INSTANCE = Mappers.getMapper(LoanMapper.class);
    LoanResponseDTO toDto(Loan loan);

}