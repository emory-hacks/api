package com.example.demo.notification;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/push-tokens")
public class PushTokenController {
    private static final Pattern EXPO_PUSH_TOKEN =
            Pattern.compile("^Expo(nent)?PushToken\\[[^\\]]+]$");

    private final PushTokenRepository pushTokenRepository;

    public PushTokenController(PushTokenRepository pushTokenRepository) {
        this.pushTokenRepository = pushTokenRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Void> register(
            @RequestBody PushTokenRequest request,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = request == null ? null : request.getToken();
        if (token == null || !EXPO_PUSH_TOKEN.matcher(token.trim()).matches()) {
            return ResponseEntity.badRequest().build();
        }

        String normalized = token.trim();
        PushToken pushToken = pushTokenRepository.findByToken(normalized).orElseGet(PushToken::new);
        pushToken.setToken(normalized);
        pushToken.setEmail(authentication.getName());
        pushToken.setUpdatedAt(LocalDateTime.now());
        pushTokenRepository.save(pushToken);
        return ResponseEntity.ok().build();
    }
}
