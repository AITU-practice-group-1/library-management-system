package com.example.librarymanagementsystem.DTOs.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "password must be at least 6 characters long")
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
