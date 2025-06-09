package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
