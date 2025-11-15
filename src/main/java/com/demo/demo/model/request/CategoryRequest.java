package com.demo.demo.model.request;

import lombok.Data;

@Data
public class CategoryRequest {
    private long id;
    private String name;
    private String description;
}
