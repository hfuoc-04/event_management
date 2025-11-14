package com.demo.demo.model.reponse;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RegisterResponse {
    private long id;
    private long accountId;
    private long eventId;
    private Date checkInTime;
    private List<String> images;
}
