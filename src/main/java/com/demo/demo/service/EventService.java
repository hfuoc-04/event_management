package com.demo.demo.service;

import com.demo.demo.entity.Category;
import com.demo.demo.entity.Event;
import com.demo.demo.entity.EventStatus;
import com.demo.demo.exception.exceptions.BadRequestException;
import com.demo.demo.model.request.EventRequest;
import com.demo.demo.repository.CategoryRepository;
import com.demo.demo.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository; // Dependency for Category

    @Autowired
    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    @Transactional
    public EventRequest createEvent(EventRequest eventModel) {
        // Find the category entity using the provided categoryId
        Category category = categoryRepository.findById(eventModel.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Category not found with id: " + eventModel.getCategoryId()));

        Event event = toEntity(eventModel);
        event.setCategory(category); // Associate the category with the event

        // Set a default status for new events
        event.setStatus(EventStatus.UPCOMING);
        Event savedEvent = eventRepository.save(event);
        return toModel(savedEvent);
    }

    public List<EventRequest> getAllActiveEvents() {
        return eventRepository.findAllByStatusNot(EventStatus.CANCELLED)
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public EventRequest getEventById(Long id) {
        Event event = eventRepository.findByIdAndStatusNot(id, EventStatus.CANCELLED)
                .orElseThrow(() -> new BadRequestException("Event not found with id: " + id));
        return toModel(event);
    }

    // UPDATE
    @Transactional
    public EventRequest updateEvent(Long id, EventRequest eventDetails) {
        Event event = eventRepository.findByIdAndStatusNot(id, EventStatus.CANCELLED)
                .orElseThrow(() -> new BadRequestException("Event not found with id: " + id));

        event.setName(eventDetails.getName());
        event.setDescription(eventDetails.getDescription());
        event.setLocation(eventDetails.getLocation());
        event.setStartTime(eventDetails.getStartTime());

        if (eventDetails.getStatus() != null) {
            event.setStatus(eventDetails.getStatus());
        }

        // Check if the category needs to be updated
        if (eventDetails.getCategoryId() != 0 && (event.getCategory() == null || event.getCategory().getId() != eventDetails.getCategoryId())) {
            Category category = categoryRepository.findById(eventDetails.getCategoryId())
                    .orElseThrow(() -> new BadRequestException("Category not found with id: " + eventDetails.getCategoryId()));
            event.setCategory(category);
        }

        Event updatedEvent = eventRepository.save(event);
        return toModel(updatedEvent);
    }

    // DELETE (Logical)
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findByIdAndStatusNot(id, EventStatus.CANCELLED)
                .orElseThrow(() -> new BadRequestException("Event not found with id: " + id));

        // This is the logical delete: we just change the status.
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    private EventRequest toModel(Event event) {
        EventRequest model = new EventRequest();
        model.setId(event.getId());
        model.setName(event.getName());
        model.setDescription(event.getDescription());
        model.setLocation(event.getLocation());
        model.setStatus(event.getStatus());
        model.setStartTime(event.getStartTime());
        // Map the category ID from the entity to the Model
        if (event.getCategory() != null) {
            model.setCategoryId(event.getCategory().getId());
        }
        return model;
    }

    private Event toEntity(EventRequest model) {
        Event event = new Event();
        // ID and Category are set in the main service methods, not here.
        event.setName(model.getName());
        event.setDescription(model.getDescription());
        event.setLocation(model.getLocation());
        event.setStatus(model.getStatus());
        event.setStartTime(model.getStartTime());
        return event;
    }
}
