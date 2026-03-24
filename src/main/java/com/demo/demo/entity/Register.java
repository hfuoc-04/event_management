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
    private long id;

    // --- QUAN TRỌNG: Định nghĩa kiểu lưu trữ thời gian ---
    @Column(name = "check_in_time")
    @Temporal(TemporalType.TIMESTAMP) // Lưu cả ngày và giờ
    private Date checkInTime;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "register_image",
            joinColumns = @JoinColumn(name = "register_id")
    )
    @Column(name = "images", nullable = false)
    private List<String> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

}
