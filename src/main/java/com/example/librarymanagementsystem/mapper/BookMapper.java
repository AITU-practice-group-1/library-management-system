package com.example.librarymanagementsystem.mapper;

import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.Entities.Book;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookDTO bookDTO);

    BookDTO toDTO(Book book);
}