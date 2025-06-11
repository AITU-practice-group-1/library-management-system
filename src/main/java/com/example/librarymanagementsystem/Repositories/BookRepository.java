package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

}