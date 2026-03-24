package com.demo.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;


@Entity
@Getter
@Setter
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    public long id;

    public String email;
    public String phone;
    public String password;
    public String fullName;
    Date dateOfBirth;
    String image;


    @Enumerated(EnumType.STRING)
    public Gender gender;

    @Enumerated(EnumType.STRING)
    public Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Register> registers;



}
