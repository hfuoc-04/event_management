package com.demo.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private float balance;

    @OneToMany(mappedBy = "from")
    private List<Transaction> sentTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "to")
    private List<Transaction> receivedTransactions = new ArrayList<>();

    // A wallet belongs to EITHER an Account OR a Store, but not both.
    // Making them optional allows for this.
    @OneToOne
    @JoinColumn(name = "account_id", unique = true)
    private Account account;

    @OneToOne
    @JoinColumn(name = "store_id", unique = true)
    private Store store;

    public Wallet(float initialBalance) {
        this.balance = initialBalance;
    }
}
