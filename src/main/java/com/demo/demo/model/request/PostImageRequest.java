package com.demo.demo.model.request;

import lombok.Data;

import java.util.List;
@Data
public class PostImageRequest {
    private List<String> imageUrls;
}
