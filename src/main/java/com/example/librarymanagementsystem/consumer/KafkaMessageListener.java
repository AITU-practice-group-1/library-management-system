package com.example.librarymanagementsystem.consumer;

import com.example.librarymanagementsystem.Entities.events.NotificationEvent;
import com.example.librarymanagementsystem.Services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageListener {
    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "notification-topic", groupId = "notifier-group")
    public void consume(NotificationEvent event) {
        System.out.println("CONSUMER KAFKA is sending email to " + event.getUserEmail());

        emailService.sendEmail(event.getUserEmail(), event.getTitle(), event.getMessage());
    }

}
