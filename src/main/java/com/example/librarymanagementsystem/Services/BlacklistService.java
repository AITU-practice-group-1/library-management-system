package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Entities.BlacklistEntry;
import com.example.librarymanagementsystem.Entities.Loan;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.BlacklistRepository;
import com.example.librarymanagementsystem.Repositories.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;
    private final LoanRepository loanRepository;

    // Ежедневная проверка просроченных книг
    @Scheduled(cron = "0 0 2 * * *") // каждый день в 2:00
    public void analyzeAndUpdateBlacklist() {
        List<Loan> overdueLoans = loanRepository.findAllByDueDateBeforeAndStatus(
                LocalDateTime.now(), Loan.LoanStatus.BORROWED
        );

        for (Loan loan : overdueLoans) {
            User user = loan.getUser();
            long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDateTime.now());

            List<BlacklistEntry> existing = blacklistRepository.findByUserAndResolvedFalse(user);

            // Добавляем новую запись если нет активной
            if (existing.isEmpty()) {
                blacklistRepository.save(BlacklistEntry.builder()
                        .user(user)
                        .reason("Просрочка возврата книги")
                        .daysOverdue(daysOverdue)
                        .resolved(false)
                        .build());
            } else {
                for (BlacklistEntry entry : existing) {
                    entry.setDaysOverdue(daysOverdue);
                }
                blacklistRepository.saveAll(existing);
            }
        }
    }

    public List<BlacklistEntry> getActiveBlacklistedUsers() {
        return blacklistRepository.findAllByResolvedFalse();
    }
}


