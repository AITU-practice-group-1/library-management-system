package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
            throw new Exception("Can not Add User \n" + e.getMessage());
        }
    }

    public UserDTO getAuhtenticatedUser() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) auth.getPrincipal();
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
    public void deleteUserById(UUID id) throws Exception {
        try {
            userRepository.deleteById(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("CAn not delete User by Id" + e.getMessage());
        }
    }
    public void updateUser(UUID id, UserDTO dto) throws Exception {
        try{
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
        } catch(Exception e){
            System.out.println(e.getMessage());
            throw new Exception("Can not update User" + e.getMessage());
        }

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
