package com.example.demo.event;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {
    private final EventRepository eventRepository;

    public ScheduleController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/schedule")
    public List<ScheduleEventResponse> getSchedule() {
        return eventRepository.findAllByOrderByStartTimeAsc()
                .stream()
                .map(ScheduleEventResponse::from)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/schedule")
    public ScheduleEventResponse createSchedule(@RequestBody CreateScheduleRequest request) {
        Event event = new Event();
        event.setTitle(request.name());
        event.setLocation(request.location());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        return ScheduleEventResponse.from(eventRepository.save(event));
    }
}
