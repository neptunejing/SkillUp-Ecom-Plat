package com.skillup.ratelimiter.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity handleRateLimitException(RateLimitException e) {
        return ResponseEntity.status(429).body(e.getMessage());
    }
}
