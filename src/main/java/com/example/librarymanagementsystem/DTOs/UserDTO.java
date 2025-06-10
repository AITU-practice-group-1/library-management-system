package com.example.librarymanagementsystem.DTOs;


import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserDTO {
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "password must be at least 6 characters long")
    private String password;
    @NotBlank
    private String role;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
