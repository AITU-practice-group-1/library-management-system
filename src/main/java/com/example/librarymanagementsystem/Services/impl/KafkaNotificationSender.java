package com.example.librarymanagementsystem.Services.impl;

import com.example.librarymanagementsystem.Entities.events.NotificationEvent;
import com.example.librarymanagementsystem.Services.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class KafkaNotificationSender implements NotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(KafkaNotificationSender.class);

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Autowired
    public KafkaNotificationSender(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendNotification(String userEmail, String title, String message) {
        NotificationEvent event = new NotificationEvent(userEmail, title, message);

        kafkaTemplate.send("notification-topic", event).whenComplete((result, ex) -> {
            if (ex != null) {
                logger.error("Failed to send notification: {}", ex.getMessage());
            } else {
                logger.info("Successfully sent notification to topic 'notification-topic' for user: {}", userEmail);
            }
        });
    }

    @Override
    public void notifyDueDate(String userEmail, String bookName, String authorName, LocalDateTime dueDate) {
        String message = String.format(
                "You borrowed book %s by %s, please do not forget to return it by %s.",
                bookName, authorName, dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        );
        sendNotification(userEmail, "Loan Due Date Reminder", message);
    }
}
