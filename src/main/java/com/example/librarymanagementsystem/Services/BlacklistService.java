package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Entities.BlacklistEntry;
import com.example.librarymanagementsystem.Entities.Loan;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.BlacklistRepository;
import com.example.librarymanagementsystem.Repositories.LoanRepository;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;
    private final UserRepository userRepository;

    public List<BlacklistEntry> getActiveBannedUsers() {
        return blacklistRepository.findByIsBanTrueAndResolvedFalse();
    }

    public List<BlacklistEntry> getActiveViolations() {
        return blacklistRepository.findByIsBanFalseAndResolvedFalse();
    }

    public void handleViolation(User user, String reason) {
        BlacklistEntry violation = BlacklistEntry.builder()
                .user(user)
                .reason(reason)
                .isBan(false)
                .resolved(false)
                .build();
        blacklistRepository.save(violation);

        long activeViolations = blacklistRepository.findByUserAndIsBanFalseAndResolvedFalse(user).size();
        if (activeViolations >= 3 && !user.isBanned()) {
            BlacklistEntry banEntry = BlacklistEntry.builder()
                    .user(user)
                    .reason("Автоматический бан: 3 активных нарушения")
                    .isBan(true)
                    .resolved(false)
                    .build();
            blacklistRepository.save(banEntry);
            user.setBanned(true);
            userRepository.save(user);
        }
    }

    public void banUserManually(User user, String reason) {
        BlacklistEntry banEntry = BlacklistEntry.builder()
                .user(user)
                .reason(reason)
                .isBan(true)
                .resolved(false)
                .build();
        blacklistRepository.save(banEntry);
        user.setBanned(true);
        userRepository.save(user);
    }

    public void unbanUser(User user) {
        user.setBanned(false);
        userRepository.save(user);
        List<BlacklistEntry> activeBans = blacklistRepository.findByUserAndIsBanTrueAndResolvedFalse(user);
        for (BlacklistEntry entry : activeBans) {
            entry.setResolved(true);
        }
        blacklistRepository.saveAll(activeBans);
    }

    public void forgiveUserViolations(User user) {
        List<BlacklistEntry> activeViolations = blacklistRepository.findByUserAndIsBanFalseAndResolvedFalse(user);
        for (BlacklistEntry v : activeViolations) {
            v.setResolved(true);
        }
        blacklistRepository.saveAll(activeViolations);
    }

    @Scheduled(cron = "0 0 3 * * ?") // каждый день в 3:00
    public void resolveOldViolations() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(60);
        List<BlacklistEntry> oldViolations = blacklistRepository.findByIsBanFalseAndResolvedFalseAndAddedAtBefore(cutoff);
        for (BlacklistEntry entry : oldViolations) {
            entry.setResolved(true);
        }
        blacklistRepository.saveAll(oldViolations);
    }

    public boolean isUserBanned(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.isBanned();
    }

    public List<BlacklistEntry> findAll() {
        return blacklistRepository.findAll(Sort.by(Sort.Direction.DESC, "addedAt"));
    }

}