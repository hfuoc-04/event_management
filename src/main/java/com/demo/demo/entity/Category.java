package com.demo.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    String name;
    String description;

    @OneToMany(mappedBy = "category",  cascade = CascadeType.ALL, orphanRemoval = true)
    List<Event> events;
}