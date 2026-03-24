package com.demo.demo.model.reponse;

import com.demo.demo.entity.Gender;
import com.demo.demo.entity.Role;
import lombok.Data;

@Data
public class AccountResponse {
    private long id;
    public String email;
    public String phone;
    public String fullName;
    public Gender gender;
    public Role role;
    public String token;
    public String image;
}
