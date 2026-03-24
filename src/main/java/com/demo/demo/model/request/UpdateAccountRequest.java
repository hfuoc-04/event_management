package com.demo.demo.model.request;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateAccountRequest {
    private String fullName;
    private String gender;
    private Date dateOfBirth;
    private String phone;
    String image;
}
