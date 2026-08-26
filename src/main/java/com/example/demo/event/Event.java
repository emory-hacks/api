package com.example.demo.event;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int points = 0;

    public Event() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getPoints() {return points;}
    public void setPoints(int points) {this.points = points;}
}
