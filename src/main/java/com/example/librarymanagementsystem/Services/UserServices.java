package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServices {
    private final UserRepository userRepository;
    public UserServices(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public void login(User user) {
        user.setCreatedAt(LocalDateTime.now().toString());
        user.setUpdatedAt(LocalDateTime.now().toString());
        try{
            userRepository.save(user);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException("Can not Add User");
        }
    }
}
