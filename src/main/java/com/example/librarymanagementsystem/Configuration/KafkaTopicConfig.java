package com.example.librarymanagementsystem.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name("notification-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic consumerOffsetsTopic() {
        return TopicBuilder.name("__consumer_offsets")
                .partitions(50)  // Matches Kafka's default for __consumer_offsets
                .replicas(1)     // Matches single-broker setup
                .config("cleanup.policy", "compact")
                .config("compression.type", "producer")
                .config("segment.bytes", "104857600")
                .build();
    }
}
