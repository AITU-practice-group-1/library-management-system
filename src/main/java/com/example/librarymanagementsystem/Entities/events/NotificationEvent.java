package com.example.librarymanagementsystem.Entities.events;

import lombok.Data;

@Data
public class NotificationEvent {
    private String userEmail;
    private String title;
    private String message;
}
