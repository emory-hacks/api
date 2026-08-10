package com.example.demo.user;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity@Table(name = "announcements")
public class Announcement {
    @Id
    private String title;
    private String content;
    private String publisher;
    private String publisherName;
    @Column(name = "created_at", nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt;
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
    public String getPublisher() {return publisher;}
    public void setPublisher(String publisher) {this.publisher = publisher;}
    public String getPublisherName() {return publisherName;}
    public void setPublisherName(String publisherName) {this.publisherName = publisherName;}
}
