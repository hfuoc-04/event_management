package com.demo.demo.controller;

import com.demo.demo.model.reponse.RegisterResponse;
import com.demo.demo.model.request.RegisterRequest;
import com.demo.demo.service.RegisterEventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register-event")
@SecurityRequirement(name = "api")
public class RegisterEventController {
    private final RegisterEventService registerService;

    @Autowired
    public RegisterEventController(RegisterEventService registerService) {
        this.registerService = registerService;
    }


    @PostMapping
    public ResponseEntity<RegisterResponse> registerForEvent(@RequestBody RegisterRequest requestModel) {
        RegisterResponse createdRegister = registerService.registerForEvent(requestModel);
        return new ResponseEntity<>(createdRegister, HttpStatus.CREATED);
    }

}
