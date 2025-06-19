package com.example.librarymanagementsystem.Configuration;

import com.example.librarymanagementsystem.Entities.ActiveSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, ActiveSession> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ActiveSession> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure Jackson ObjectMapper with JSR-310 support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Enable Java 8 date/time support
        Jackson2JsonRedisSerializer<ActiveSession> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, ActiveSession.class);

        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }
}
