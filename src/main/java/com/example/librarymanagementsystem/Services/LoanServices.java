package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.loan.LoanRequestDTO;
import com.example.librarymanagementsystem.DTOs.loan.LoanResponseDTO;
import com.example.librarymanagementsystem.DTOs.loan.LoanUpdateDTO;
import com.example.librarymanagementsystem.Entities.Book;
import com.example.librarymanagementsystem.Entities.Loan;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.BlacklistRepository;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.Repositories.LoanRepository;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import com.example.librarymanagementsystem.exceptions.LoanNotFoundException;
import com.example.librarymanagementsystem.mapper.LoanMapper;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServices {

    private static final Logger logger = LoggerFactory.getLogger(LoanServices.class);

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BlacklistService blacklistService;
    private final LoanMapper loanMapper;
    
    @Transactional
    public LoanResponseDTO createLoan(LoanRequestDTO dto, User librarian) {
        logger.info("Creating loan for user ID: {} and book ID: {}", dto.getUserId(), dto.getBookId());
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (blacklistService.isUserBanned(user)) {
            throw new RuntimeException("User is banned.");
        }
        // Note: We assume book availability was handled by the Reservations service.
        // For a direct loan (no reservation), a check should be performed here.

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setIssuedBy(librarian);
        loan.setDueDate(dto.getDueDate());
        loan.setStatus(Loan.LoanStatus.BORROWED);

        Loan savedLoan = loanRepository.save(loan);
        logger.info("Loan created successfully with ID: {}", savedLoan.getId());
        return loanMapper.toResponseDto(savedLoan);
    }
      
    @Transactional
    public LoanResponseDTO updateLoan(UUID loanId, LoanUpdateDTO dto) {
        logger.info("Updating loan with ID: {}", loanId);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        // Handle book return logic
        if (dto.getStatus() == Loan.LoanStatus.RETURNED && loan.getStatus() == Loan.LoanStatus.BORROWED) {
            loan.setStatus(Loan.LoanStatus.RETURNED);
            loan.setReturnDate(LocalDateTime.now());

            // CRITICAL: Increment the available copies of the book
            Book book = loan.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
            logger.info("Book '{}' has been returned. Available copies updated.", book.getTitle());

        }

        // Allow due date extension
        if (dto.getDueDate() != null) {
            loan.setDueDate(dto.getDueDate());
            logger.info("Loan {} due date updated to: {}", loanId, dto.getDueDate());
        }

        Loan updatedLoan = loanRepository.save(loan);
        return loanMapper.toResponseDto(updatedLoan);
    }

    public List<LoanResponseDTO> findAll() {
        return loanRepository.findAll().stream()
                .map(loanMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public LoanResponseDTO findById(UUID id) {
        return loanRepository.findById(id)
                .map(loanMapper::toResponseDto)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + id));
    }

    public List<LoanResponseDTO> getLoansByUserId(UUID userId) {
        return loanRepository.findByUserId(userId).stream()
                .map(loanMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<LoanResponseDTO> findAllWithSearch(String keyword, Pageable pageable) {
        // Create a Specification from our custom search logic
        Specification<Loan> spec = createSearchSpecification(keyword);

        // Call the new findAll method provided by JpaSpecificationExecutor
        Page<Loan> loanPage = loanRepository.findAll(spec, pageable);

        return loanPage.map(loanMapper::toResponseDto);
    }
    private Specification<Loan> createSearchSpecification(String keyword) {
        // Return a lambda that implements the Specification interface's toPredicate method
        return (root, query, criteriaBuilder) -> {

            // If the keyword is empty or null, we don't add any 'where' clause.
            // criteriaBuilder.conjunction() is a placeholder that effectively means "where 1=1"
            if (!StringUtils.hasText(keyword)) {
                return criteriaBuilder.conjunction();
            }

            // Prepare the keyword for a case-insensitive LIKE search
            String likePattern = "%" + keyword.toLowerCase() + "%";

            // Define the path to the book's title: from Loan (root) -> join Book -> get title
            Predicate titlePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("book").get("title")),
                    likePattern
            );

            // Define the path to the user's email: from Loan (root) -> join User -> get email
            Predicate emailPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("user").get("email")),
                    likePattern
            );

            // Combine the two predicates with an OR condition
            // This creates the final clause: "where lower(book.title) like '%keyword%' OR lower(user.email) like '%keyword%'"
            return criteriaBuilder.or(titlePredicate, emailPredicate);
        };
    }
}