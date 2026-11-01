package com.apartment.security;

import com.apartment.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Login attempt tracking disabled - no account lockout
//@Service
public class LoginAttemptService {

    private final UserRepository userRepository;
    private final int MAX_ATTEMPTS = 5;
    private final long LOCK_TIME_DURATION = 30 * 60 * 1000; // 30 minutes

    private final ConcurrentHashMap<String, AtomicInteger> attemptsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> lockCache = new ConcurrentHashMap<>();

    public LoginAttemptService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void loginFailed(String key) {
        // Disabled - no action
    }

    public void loginSucceeded(String key) {
        // Disabled - no action
    }

    public boolean isBlocked(String key) {
        return false;
    }

    public void lockUserAccount(String username) {
        // Disabled - no action
    }

    public void resetUserAttempts(String username) {
        // Disabled - no action
    }
}
