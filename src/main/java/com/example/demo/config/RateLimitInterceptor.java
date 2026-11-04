package com.example.demo.config;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final SlidingWindowRateLimiter limiter;

    public RateLimitInterceptor(
            @Value("${app.rate-limit.max-requests:20}") int maxRequests,
            @Value("${app.rate-limit.window-seconds:10}") int windowSeconds) {
        this(new SlidingWindowRateLimiter(maxRequests, windowSeconds * 1000L));
    }

    RateLimitInterceptor(SlidingWindowRateLimiter limiter) {
        this.limiter = limiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (request.getDispatcherType() == DispatcherType.ERROR
                || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        int retryAfterSeconds = limiter.tryAcquire(clientKey(request));
        if (retryAfterSeconds == 0) {
            return true;
        }
        response.setStatus(429);
        response.setHeader("Retry-After", Integer.toString(retryAfterSeconds));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"Too many requests. Try again shortly.\"}");
        return false;
    }

    private static String clientKey(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getName() != null
                && !authentication.getName().isBlank()) {
            return "user:" + authentication.getName();
        }
        return "ip:" + clientIp(request);
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            String client = comma >= 0 ? forwarded.substring(0, comma) : forwarded;
            if (!client.isBlank()) {
                return client.trim();
            }
        }
        return request.getRemoteAddr();
    }
}
