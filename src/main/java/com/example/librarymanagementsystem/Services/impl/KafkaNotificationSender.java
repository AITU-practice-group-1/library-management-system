package com.example.librarymanagementsystem.Services.impl;

import com.example.librarymanagementsystem.Entities.events.NotificationEvent;
import com.example.librarymanagementsystem.Services.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class KafkaNotificationSender implements NotificationSender {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Autowired
    public KafkaNotificationSender(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendNotification(String userEmail, String title, String message) {
        NotificationEvent event = new NotificationEvent();
        event.setUserEmail(userEmail);
        event.setTitle(title);
        event.setMessage(message);
        kafkaTemplate.send("notification-topic", event);
    }
}
