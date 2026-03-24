package com.demo.demo.model.request;

import lombok.Data;

@Data
public class UpdateFaceRequest {
    private String email;
    private String faceImageUrl;
}
