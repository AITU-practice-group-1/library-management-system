package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.ShortProjections.UserProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    List<UserProjection> findAllBy();
    List<UserProjection> findAllByRole(String role);
//    @Query("SELECT u.id, u.email, u.firstName, u.lastName FROM User u WHERE u.id = :id")
//    Optional<UserDTO> findUserDtoById(UUID id);
@Query("SELECT new com.example.librarymanagementsystem.DTOs.Users.UserDTO(u.id, u.email, u.firstName, u.lastName, COUNT(l) as totalRead) " +
        "FROM User u JOIN Loan l ON u.id = l.user.id " +
        "WHERE l.status = 'RETURNED' " +
        "GROUP BY u.id, u.email, u.firstName, u.lastName " +
        "ORDER BY COUNT(l) DESC")
        List<UserDTO> findTopUsers(Pageable pageable);
}
