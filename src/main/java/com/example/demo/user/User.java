package com.example.demo.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;
    private String password;
    private String name;

    @Column(name = "team_name")
    private String teamName;

    private String role;

    @Column(name = "qr_token")
    private String qrToken;

    private int score = 0;

    @Column(name = "checked_in")
    private boolean checkedIn = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "num_events")
    private int numEvents = 0;

    private int points = 0;

    @Column(unique = true)
    private String username;

    public User() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (role == null) {
            role = "participant";
        }
        if (qrToken == null) {
            qrToken = UUID.randomUUID().toString(); //placeholder for actual qr code string
        }
        if (name == null && email != null) {
            name = email.split("@")[0];
        }
    }

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

    public String getQrToken() { return qrToken; }
    public void setQrToken(String qrToken) { this.qrToken = qrToken; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public boolean isCheckedIn() { return checkedIn; }
    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getNumEvents() { return numEvents; }
    public void setNumEvents(int numEvents) { this.numEvents = numEvents; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
