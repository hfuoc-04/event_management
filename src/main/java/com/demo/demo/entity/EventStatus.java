package com.demo.demo.entity;

public enum EventStatus {
    UPCOMING, // The event is scheduled for the future
    IN_PROGRESS, // The event is currently happening
    COMPLETED,   // The event has finished
    CANCELLED  // The event has been logically deleted
}
