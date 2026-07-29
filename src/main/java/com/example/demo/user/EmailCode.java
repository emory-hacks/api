package com.example.demo.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_code")
public class EmailCode {
    @Id
    private String email;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private LocalDateTime expiration;

    public EmailCode() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }
}
