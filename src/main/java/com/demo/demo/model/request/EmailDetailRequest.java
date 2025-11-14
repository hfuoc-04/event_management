package com.demo.demo.model.request;

import lombok.Data;

@Data
public class EmailDetailRequest {
    private String recipient;
    private String subject;
}
