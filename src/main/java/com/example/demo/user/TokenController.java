package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tokens")
public class TokenController {
    @Autowired
    private ScanTokenRepository tokenRepository;
    @PostMapping("/generate")
    public ResponseEntity<?> generateToken(Authentication authentication){
        String email = authentication.getName();
        ScanToken token = new ScanToken();
        token.setUserEmail(email);
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        ScanToken savedToken = tokenRepository.save(token);
        return ResponseEntity.ok(savedToken);
    }

}
