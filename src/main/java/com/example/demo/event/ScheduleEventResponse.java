package com.example.demo.event;

import java.time.LocalDateTime;

public record ScheduleEventResponse(
        Long id,
        String name,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime) {

    public static ScheduleEventResponse from(Event event) {
        return new ScheduleEventResponse(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime());
    }
}
