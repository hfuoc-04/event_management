package com.demo.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    String name;
    String description;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @OneToOne
    Wallet wallet;
}
