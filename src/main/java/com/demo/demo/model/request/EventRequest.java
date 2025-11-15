package com.demo.demo.model.request;

import com.demo.demo.entity.EventStatus;
import lombok.Data;

import java.util.Date;

@Data
public class EventRequest {
    private long id;
    private String name;
    private String description;
    private String location;
    private EventStatus status;
    private Date startTime;
    long categoryId;
}
