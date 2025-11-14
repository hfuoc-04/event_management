package com.demo.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY) // Use LAZY fetching for performance
    @JoinColumn(name = "from_wallet_id", nullable = false)
    private Wallet from;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_wallet_id", nullable = false)
    private Wallet to;

    @Column(nullable = false)
    private float amount; // CRITICAL: The amount of the transaction

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status; // CRITICAL: Track if the transaction succeeded

    @Column(nullable = false)
    private LocalDateTime timestamp; // CRITICAL: When the transaction happened

    public Transaction(Wallet from, Wallet to, float amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.status = TransactionStatus.PENDING; // Start as pending
        this.timestamp = LocalDateTime.now();
    }
}