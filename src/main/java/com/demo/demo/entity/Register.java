package com.demo.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Register {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    Date checkInTime;

    @ElementCollection(fetch = FetchType.LAZY) // LAZY is best practice for collections
    @CollectionTable(
            name = "register_image", // Name of the table that will store the tags
            joinColumns = @JoinColumn(name = "register_id") // Column that links back to the Event
    )
    @Column(name = "images", nullable = false) // Name of the column that will store the actual string tag
    private List<String> images = new ArrayList<>(); // IMPORTANT: Always initialize collections!

    @ManyToOne
    @JoinColumn(name = "account_id")
    Account account;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

}
