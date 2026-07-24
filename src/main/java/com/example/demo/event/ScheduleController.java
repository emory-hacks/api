package com.example.demo.event;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/schedule/{id}")
    public ScheduleEventResponse getScheduleById(@PathVariable long id) {
        return eventRepository.findById(id)
                .map(ScheduleEventResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/schedule/{id}")
    public ScheduleEventResponse updateSchedule(
            @PathVariable long id,
            @RequestBody CreateScheduleRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        event.setTitle(request.name());
        event.setLocation(request.location());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());

        return ScheduleEventResponse.from(eventRepository.save(event));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/schedule/{id}")
    public Map<String, String> deleteSchedule(@PathVariable long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        eventRepository.delete(event);
        return Map.of("message", "Event deleted");
    }
}
