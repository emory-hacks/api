package com.example.demo.event;

import java.time.LocalDateTime;

public record CreateScheduleRequest(
        String name,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime) {
}
