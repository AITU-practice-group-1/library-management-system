package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Entities.EmailVerificationToken;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.EmailTokenRepository;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class EmailTokenService {
    private final UserRepository userRepository;
    private final EmailTokenRepository emailTokenRepository;

    public EmailTokenService(EmailTokenRepository emailTokenRepository, UserRepository userRepository){
        this.emailTokenRepository = emailTokenRepository;
        this.userRepository = userRepository;
    }

    public void saveToken(String token, User user){
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUser(user);
        emailVerificationToken.setExpiryDate(java.time.LocalDateTime.now().plusDays(1));
        emailTokenRepository.save(emailVerificationToken);
    }

    public EmailVerificationToken findByToken(String token){
        return emailTokenRepository.findByToken(token);
    }

    public void confirmToken(String token){
        EmailVerificationToken tokenEntity = emailTokenRepository.findByToken(token);
        if(tokenEntity == null){
            throw new RuntimeException("Invalid Token");
        }
        if(tokenEntity.getExpiryDate().isBefore(java.time.LocalDateTime.now())){
            throw new RuntimeException("Token has expired");
        }
        User user = tokenEntity.getUser();
        user.setActive(true);
        userRepository.save(user);
        emailTokenRepository.delete(tokenEntity);
    }
}
