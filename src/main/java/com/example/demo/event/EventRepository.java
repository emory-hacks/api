package com.example.demo.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    List<Event> findAllByOrderByStartTimeAsc();

    Optional<Event> findFirstByEndTimeGreaterThanEqualOrderByStartTimeAsc(LocalDateTime time);
    @Query(value = "SELECT * FROM events WHERE start_time >= (NOW() AT TIME ZONE 'America/New_York') AND start_time <= (NOW() AT TIME ZONE 'America/New_York') + INTERVAL '30 minutes' ORDER BY start_time ASC", nativeQuery = true)
    List<Event> findUpcomingEventsInNext30Minutes();
}
