package com.example.librarymanagementsystem.mapper;


import com.example.librarymanagementsystem.DTOs.LoanDTO;
import com.example.librarymanagementsystem.Entities.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel="spring", uses = {UserMapper.class})
public interface LoanMapper{
    LoanDTO toDto (Loan loan);
    Loan toEntity(LoanDTO dto);

}
