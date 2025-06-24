package com.example.librarymanagementsystem.DTOs.Users;


import lombok.Data;

import javax.validation.constraints.*;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
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
    private Long totalRead;
    private String imageUrl;
    private String imageId;

    public UserDTO() {

    }

    public UserDTO(UUID id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserDTO(UUID id, String email, String firstName, String lastName, Long totalRead) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalRead = totalRead;
    }


    public UserDTO(UUID id, String email, String firstName, String lastName, String role, String password, String imageUrl, String imageId) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.password = password;
        this.imageUrl = imageUrl;
        this.imageId = imageId;
    }
}
