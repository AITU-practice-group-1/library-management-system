package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserServices {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final  EmailTokenService emailTokenService;
    private final EmailService emailService ;
    public UserServices(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailTokenService emailTokenService, EmailService emailService)
    {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailTokenService = emailTokenService;
    }

    public void register(UserDTO dto) throws Exception {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setActive(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        try{
            userRepository.save(user);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("Can not Add User \n" + e.getMessage());
        }
        String token = UUID.randomUUID().toString();
        try {
            emailTokenService.saveToken(token, user);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("Can not send email verification token \n" + e.getMessage());
        }
        String link = "http://localhost:8080/users/confirm?token=" + token;
        emailService.sendEmail(user.getEmail(), "Confirm your email", "Click: " + link);

    }

    public UserDTO getAuthenticatedUser() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = userRepository.findByEmail(auth.getName()).orElseThrow(() -> new Exception("User not found"));
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(userDetails.getUsername());
        userDTO.setRole(userDetails.getAuthorities().iterator().next().getAuthority());
        userDTO.setFirstName(userDetails.getFirstName());
        userDTO.setLastName(userDetails.getLastName());
        userDTO.setId(userDetails.getId());
        return userDTO;
    }


    public List<User> getAllUsers() throws Exception {
        List<User> users;
        try {
            users = userRepository.findAll();
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("CAn not get All Users \n" + e.getMessage());
        }
        return users;
    }
    public User getUserById(UUID id) throws Exception {
        User user;
        try {
            user = userRepository.findById(id).orElseThrow(() -> new Exception("User not found"));
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("CAn not get User by Id" + e.getMessage());
        }
        return user;
    }
    public Optional<User> getUserByEmail(String email) throws Exception {
        try{
            return userRepository.findByEmail(email);
        }
        catch (Exception e){
            throw new Exception("CAn not get User by Email" + e.getMessage());
        }
    }
    public void deleteUserById(UUID id) throws Exception {
        User user = getUserByEmail("hui").orElseThrow();
        try {
            userRepository.deleteById(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("CAn not delete User by Id" + e.getMessage());
        }
    }
    public void updateUser(UserDTO dto) throws Exception {
        try{
            UUID id = getAuthenticatedUser().getId()    ;
            User user = getUserById(id);
            if(dto.getEmail() != null)
            {
                user.setEmail(dto.getEmail());
            }
            if(dto.getPassword() != null)
            {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            if(dto.getRole() != null)
            {
                user.setRole(dto.getRole());
            }
            if(dto.getFirstName() != null){
                user.setFirstName(dto.getFirstName());
            }
            if(dto.getLastName() != null){
                user.setLastName(dto.getLastName());
            }
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } catch(Exception e){
            System.out.println(e.getMessage());
            throw new Exception("Can not update User" + e.getMessage());
        }

    }

    public Page<User> getAllUsersPaginated(Pageable pageable) throws Exception {
        Page<User> allUsers;
        try{
            allUsers = userRepository.findAll(pageable);
        }
        catch (Exception e)
        {throw new Exception("CAn not get users paginated \n"  + e.getMessage());}
        return allUsers;
    }

    public List<User> getAllUsersByRole(String role) throws Exception {
        List<User> allUsersByRole;
        try{
            allUsersByRole = userRepository.findByRole(role);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            throw new Exception("CAn not get users by role \n"  + e.getMessage());
        }
        return allUsersByRole;
    }
}