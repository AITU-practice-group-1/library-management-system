package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.UserDTO;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServices {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserServices(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(UserDTO dto) throws Exception {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        try{
            userRepository.save(user);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("Can not Add User");
        }
    }
}
