package com.example.librarymanagementsystem.ShortProjections;

import java.time.LocalDate;
import java.util.UUID;

public interface UserProjection {
    UUID getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getRole();
//    LocalDate getCreatedAt();
//    LocalDate getUpdatedAt();
}