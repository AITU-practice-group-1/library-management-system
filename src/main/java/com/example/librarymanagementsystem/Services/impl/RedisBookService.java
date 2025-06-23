package com.example.librarymanagementsystem.Services.impl;

import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class RedisBookService {
    private final RedisTemplate<String, Object> redisBookTemplate;

    public RedisBookService(RedisTemplate<String, Object> redisTemplate) {
        this.redisBookTemplate = redisTemplate;
    }

    public void saveBookPage(String key, Page<BookDTO> bookPage) {
        redisBookTemplate.opsForValue().set(key, bookPage);
    }
    public Page<BookDTO> getBookPage(String key) {
        return (Page<BookDTO>) redisBookTemplate.opsForValue().get(key);
    }
    public void deleteBookPage(String key) {
        redisBookTemplate.delete(key);
    }
    public boolean containsBookPage(String key) {
        return Boolean.TRUE.equals(redisBookTemplate.hasKey(key));
    }
}
