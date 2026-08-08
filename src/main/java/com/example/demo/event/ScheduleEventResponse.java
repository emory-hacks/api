package com.example.demo.event;

import java.time.LocalDateTime;

public record ScheduleEventResponse(
        String title,
        String body,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime) {

    public static ScheduleEventResponse from(Event event) {
        return new ScheduleEventResponse(
                event.getTitle(),
                event.getBody(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime());
    }
}
