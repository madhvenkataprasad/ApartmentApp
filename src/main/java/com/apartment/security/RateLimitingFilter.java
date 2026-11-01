package com.apartment.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Rate limiting disabled - no account lockout
//@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastResetTime = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS_PER_MINUTE = 20;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String key = getClientIP(httpRequest);
        LocalDateTime now = LocalDateTime.now();

        // Reset counter if more than a minute has passed
        LocalDateTime lastReset = lastResetTime.get(key);
        if (lastReset == null || lastReset.plusMinutes(1).isBefore(now)) {
            requestCounts.put(key, 0);
            lastResetTime.put(key, now);
        }

        // Increment counter
        int count = requestCounts.getOrDefault(key, 0) + 1;
        requestCounts.put(key, count);

        if (count > MAX_REQUESTS_PER_MINUTE) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
