package com.example.librarymanagementsystem.mapper;


import com.example.librarymanagementsystem.DTOs.LoanDTO;
import com.example.librarymanagementsystem.Entities.Loan;
import com.example.librarymanagementsystem.Entities.Reservations;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel="spring", uses = {UserMapper.class})
public interface LoanMapper{
    LoanDTO toDto (Loan loan);
    Loan toEntity(LoanDTO dto);

    @Mapping(target = "issuedBy", ignore = true)
    @Mapping(target = "issueDate", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "returnDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    LoanDTO fromReservation(Reservations reservation);

    @org.mapstruct.AfterMapping
    default void enrichDto(@org.mapstruct.MappingTarget LoanDTO dto, Loan loan) {
        if (loan.getUser() != null) {
            dto.setUserEmail(loan.getUser().getEmail());
        }
        if (loan.getBook() != null) {
            dto.setBookTitle(loan.getBook().getTitle());
        }
        if (loan.getIssuedBy() != null) {
            dto.setIssuedByEmail(loan.getIssuedBy().getEmail());
        }
    }
}
