package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Entities.PasswordResetToken;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.PasswordResetTokenRepository;
import com.example.librarymanagementsystem.Repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void createPasswordResetToken(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()){
                throw new RuntimeException("User not found");
            }
            User user = optionalUser.get();

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
            passwordResetTokenRepository.save(resetToken);

            String resetLink = "http://localhost:8080/reset-password?token=" + token;
            String subject = "Password Reset";
            String body = "To reset your password, follow the link" + resetLink;
            emailService.sendEmail(user.getEmail(), subject, body);
        }
        public void passwordReset(String token, String newPassword){
            PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
            if (passwordResetToken == null){
                throw new RuntimeException("Token not found");
            }

            if(passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Token expired");
            }
            User user = passwordResetToken.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            passwordResetTokenRepository.delete(passwordResetToken);
        }
    }

