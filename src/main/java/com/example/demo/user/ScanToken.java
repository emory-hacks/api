package com.example.demo.user;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scan_tokens")
public class ScanToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID token;
    @Column(nullable = false)
    private String userEmail;
    @Column(nullable = false)
    boolean isUsed = false;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public ScanToken() {
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }


}


