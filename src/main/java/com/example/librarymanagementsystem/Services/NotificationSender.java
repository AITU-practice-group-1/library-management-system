package com.example.librarymanagementsystem.Services;

public interface NotificationSender {
    void sendNotification(String userEmail, String title, String message);
}
