package com.example.demo.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByStartTimeAsc();

    Optional<Event> findFirstByEndTimeGreaterThanEqualOrderByStartTimeAsc(LocalDateTime time);
}