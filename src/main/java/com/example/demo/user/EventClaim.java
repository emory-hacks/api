package com.example.demo.user;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "event_claims")
public class EventClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String userEmail;
    @Column(nullable = false)
    private String eventId;
    @Column(nullable = false)
    private Integer pointsAwarded;
    private LocalDateTime claimedAt = LocalDateTime.now();
    public EventClaim(){}
    public Long getId(){
        return id;
    }
    public String getUserEmail(){
        return userEmail;
    }
    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }
    public String getEventId(){
        return this.eventId;
    }
    public void setEventId(String eventId){
        this.eventId = eventId;
    }
    public Integer getPointsAwarded(){
        return pointsAwarded;
    }
    public void setPointsAwarded(Integer pointsAwarded){
        this.pointsAwarded = pointsAwarded;
    }


}
