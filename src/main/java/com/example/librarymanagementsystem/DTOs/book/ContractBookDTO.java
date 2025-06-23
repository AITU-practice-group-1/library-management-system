package com.example.librarymanagementsystem.DTOs.book;

import lombok.Data;

@Data
public class ContractBookDTO {
    private String title;
    private String author;
    private String isbn;

    public ContractBookDTO(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }
}
