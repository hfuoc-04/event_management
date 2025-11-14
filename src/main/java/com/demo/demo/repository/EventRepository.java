package com.demo.demo.repository;

import com.demo.demo.entity.Event;
import com.demo.demo.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndStatusNot(Long id, EventStatus status);
    List<Event> findAllByStatusNot(EventStatus status);
}
