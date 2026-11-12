package com.example.demo.user;

import java.util.UUID;

public class CheckInRequest {
    private String email;
    private UUID token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }
}
