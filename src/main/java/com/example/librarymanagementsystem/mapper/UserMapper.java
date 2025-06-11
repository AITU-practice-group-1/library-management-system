package com.example.librarymanagementsystem.mapper;

import com.example.librarymanagementsystem.Entities.User;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {
    default UUID map(User user) {
        return user != null ? user.getId() : null;
    }

    default User map(UUID id) {
        if (id == null) return null;
        User user = new User();
        user.setId(id);
        return user;
    }
}
