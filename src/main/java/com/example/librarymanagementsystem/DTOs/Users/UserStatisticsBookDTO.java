package com.example.librarymanagementsystem.DTOs.Users;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class UserStatisticsBookDTO {
    @NotBlank
    private String bookTitle;
    private UUID bookId;
    private int totalRead;
    private String status;

    public UserStatisticsBookDTO(String bookTitle, UUID bookId, String status) {
        this.bookTitle = bookTitle;
        this.bookId = bookId;
        this.status = status;
    }
}
