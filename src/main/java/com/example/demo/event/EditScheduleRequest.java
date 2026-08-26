package com.example.demo.event;

import java.time.LocalDateTime;

public class EditScheduleRequest {
    private String title;
    private String correctedTitle;
    private String correctedBody;
    private String correctedLocation;
    private LocalDateTime correctedStartTime;
    private LocalDateTime correctedEndTime;
    private Integer correctedPoints;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCorrectedTitle() { return correctedTitle; }
    public void setCorrectedTitle(String correctedTitle) { this.correctedTitle = correctedTitle; }
    public String getCorrectedBody() { return correctedBody; }
    public void setCorrectedBody(String correctedBody) { this.correctedBody = correctedBody; }
    public String getCorrectedLocation() { return correctedLocation; }
    public void setCorrectedLocation(String correctedLocation) { this.correctedLocation = correctedLocation; }
    public LocalDateTime getCorrectedStartTime() { return correctedStartTime; }
    public void setCorrectedStartTime(LocalDateTime correctedStartTime) { this.correctedStartTime = correctedStartTime; }
    public LocalDateTime getCorrectedEndTime() { return correctedEndTime; }
    public void setCorrectedEndTime(LocalDateTime correctedEndTime) { this.correctedEndTime = correctedEndTime; }
    public Integer getCorrectedPoints() { return correctedPoints; }
    public void setCorrectedPoints(Integer correctedPoints) { this.correctedPoints = correctedPoints; }
}
