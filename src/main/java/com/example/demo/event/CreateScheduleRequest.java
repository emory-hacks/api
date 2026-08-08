package com.example.demo.event;

import java.time.LocalDateTime;

public record CreateScheduleRequest(
        String title,
        String body,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime) {
}
