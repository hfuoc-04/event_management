package com.demo.demo.model.reponse;

import com.demo.demo.entity.Gender;
import com.demo.demo.entity.Role;
import lombok.Data;

@Data
public class RegisterAccountResponse {
    private long id;
    private String email;
    private String phone;
    private String fullName;
    private Gender gender;
    private Role role;
    private String message;
}
