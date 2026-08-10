package com.example.demo.user;

public class EditAnnouncementRequest {
    private String title;
    private String correctedTitle;
    private String correctedContent;

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getCorrectedTitle() {
        return correctedTitle;
    }

    public void setCorrectedTitle(String correctedTitle) {
        this.correctedTitle = correctedTitle;
    }

    public String getCorrectedContent() {
        return correctedContent;
    }

    public void setCorrectedContent(String correctedContent) {
        this.correctedContent = correctedContent;
    }
}
