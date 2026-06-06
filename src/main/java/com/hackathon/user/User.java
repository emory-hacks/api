package com.hackathon.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "team_name")
    private String teamName;

    @Column(nullable = false)
    private String role = "PARTICIPANT";

    @Column(name = "qr_token", unique = true, nullable = false)
    private UUID qrToken = UUID.randomUUID();

    @Column(nullable = false)
    private int score = 0;

    @Column(name = "checked_in", nullable = false)
    private boolean checkedIn = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public User() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public UUID getQrToken() { return qrToken; }
    public void setQrToken(UUID qrToken) { this.qrToken = qrToken; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public boolean isCheckedIn() { return checkedIn; }
    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}