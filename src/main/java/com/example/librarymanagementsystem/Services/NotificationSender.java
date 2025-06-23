package com.example.librarymanagementsystem.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface NotificationSender {
    void sendNotification(String userEmail, String title, String message);
    void notifyDueDate(String userEmail, String bookName, String authorName, LocalDateTime dueDate);
}
