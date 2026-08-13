package com.example.demo.event;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
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

    @PostMapping("/schedule/current")
    public Map<String, String> getCurrentEvent(@RequestBody CurrentEventRequest request) {
        if (request.currentTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currentTime is required");
        }
        return eventRepository
                .findFirstByEndTimeGreaterThanEqualOrderByStartTimeAsc(request.currentTime())
                .map(event -> Map.of("title", event.getTitle()))
                .orElse(Map.of());
    }

    @GetMapping("/schedule/{title}")
    public ScheduleEventResponse getScheduleByTitle(@PathVariable String title) {
        return eventRepository.findById(title)
                .map(ScheduleEventResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/schedule")
    public ScheduleEventResponse createSchedule(@RequestBody CreateScheduleRequest request) {
        requireBody(request.body());
        if (eventRepository.existsById(request.title())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event already exists");
        }
        Event event = new Event();
        event.setTitle(request.title());
        event.setBody(request.body());
        event.setLocation(request.location());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        return ScheduleEventResponse.from(eventRepository.save(event));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/schedule/{title}")
    public ScheduleEventResponse updateSchedule(
            @PathVariable String title,
            @RequestBody CreateScheduleRequest request) {
        requireBody(request.body());
        Event event = eventRepository.findById(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        event.setBody(request.body());
        event.setLocation(request.location());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());

        return ScheduleEventResponse.from(eventRepository.save(event));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/schedule")
    @Transactional
    public ScheduleEventResponse editSchedule(@RequestBody EditScheduleRequest request) {
        String title = request.getTitle();
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
        }
        Event event = eventRepository.findById(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        String correctedTitle = request.getCorrectedTitle() != null && !request.getCorrectedTitle().isBlank()
                ? request.getCorrectedTitle()
                : title;
        String correctedBody = request.getCorrectedBody() != null ? request.getCorrectedBody() : event.getBody();
        String correctedLocation = request.getCorrectedLocation() != null ? request.getCorrectedLocation() : event.getLocation();
        var correctedStartTime = request.getCorrectedStartTime() != null ? request.getCorrectedStartTime() : event.getStartTime();
        var correctedEndTime = request.getCorrectedEndTime() != null ? request.getCorrectedEndTime() : event.getEndTime();

        if (title.equals(correctedTitle)) {
            event.setBody(correctedBody);
            event.setLocation(correctedLocation);
            event.setStartTime(correctedStartTime);
            event.setEndTime(correctedEndTime);
            return ScheduleEventResponse.from(eventRepository.save(event));
        }

        Event updated = new Event();
        updated.setTitle(correctedTitle);
        updated.setBody(correctedBody);
        updated.setLocation(correctedLocation);
        updated.setStartTime(correctedStartTime);
        updated.setEndTime(correctedEndTime);

        eventRepository.delete(event);
        eventRepository.flush();
        return ScheduleEventResponse.from(eventRepository.save(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/schedule/{title}")
    public Map<String, String> deleteSchedule(@PathVariable String title) {
        Event event = eventRepository.findById(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        eventRepository.delete(event);
        return Map.of("message", "Event deleted");
    }

    private static void requireBody(String body) {
        if (body == null || body.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body is required");
        }
    }
}
