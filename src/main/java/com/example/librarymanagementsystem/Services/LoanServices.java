package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.LoanDTO;
import com.example.librarymanagementsystem.DTOs.LoanUpdateDTO;
import com.example.librarymanagementsystem.Entities.Book;
import com.example.librarymanagementsystem.Entities.Loan;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.LoanRepository;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.mapper.LoanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServices {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanMapper loanMapper;
    public LoanDTO createLoan(LoanDTO dto){
        var user = userRepository.findById(dto.getUserId())
                .orElseThrow(()-> new RuntimeException("User not found"));
        var book = bookRepository.findById(dto.getBookId())
                .orElseThrow(()-> new RuntimeException("Book not found"));
        var issuedBy = userRepository.findById(dto.getIssuedBy())
                .orElseThrow(()-> new RuntimeException("Librarian (issuedBy) not found"));

        Loan loan = loanMapper.toEntity(dto);
        loan.setUser(user);
        loan.setBook(book);
        loan.setIssuedBy(issuedBy);
        loan.setUpdatedAt(LocalDateTime.now());
        loan.setDueDate(dto.getDueDate().atStartOfDay());
        loan.setStatus(Loan.LoanStatus.valueOf("BORROWED"));


        return loanMapper.toDto(loanRepository.save(loan));
    }


    public List<LoanDTO> findAll(){
        return loanRepository.findAll().stream().map(loanMapper::toDto).toList();
    }

    public LoanDTO findById(UUID id){
        return loanRepository.findById(id).map(loanMapper::toDto).orElseThrow(()-> new RuntimeException("Loan not found"));
    }

    public LoanDTO updateLoan(UUID id ,LoanUpdateDTO dto){
        Loan loan = loanRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Loan not found"));
        if (dto.getStatus() != null){
            loan.setStatus(dto.getStatus());
        }
        if (dto.getDueDate() != null){
            loan.setDueDate(dto.getDueDate().atStartOfDay());

        }

        loan.setUpdatedAt(LocalDateTime.now());
        System.out.println("Updating loan with ID: " + id);
        return loanMapper.toDto(loanRepository.save(loan));
    }
    public List<LoanDTO> getLoansByUserId(UUID userId) {
        return loanRepository.findByUserId(userId).stream()
                .map(loanMapper::toDto)
                .toList();
    }

}
