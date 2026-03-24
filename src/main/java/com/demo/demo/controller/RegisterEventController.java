package com.demo.demo.controller;

import com.demo.demo.model.reponse.RegisterResponse;
import com.demo.demo.model.request.RegisterRequest;
import com.demo.demo.service.RegisterEventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/register-event")
@SecurityRequirement(name = "api")
public class RegisterEventController {
    private final RegisterEventService registerService;

    @Autowired
    public RegisterEventController(RegisterEventService registerService) {
        this.registerService = registerService;
    }
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<RegisterResponse>> getRegistersByAccount(@PathVariable long accountId) {
        return ResponseEntity.ok(registerService.getRegistersByAccountId(accountId));
    }


    // 👇 THÊM API NÀY: Lấy danh sách đăng ký
    // GET /api/register-event/event/{eventId}
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<RegisterResponse>> getRegistersByEvent(@PathVariable long eventId) {
        return ResponseEntity.ok(registerService.getRegistersByEventId(eventId));
    }
    // 👇 THÊM API NÀY: Check trạng thái (để nút đăng ký đổi màu)
    // GET /api/register-event?accountId=1&eventId=2
    @GetMapping
    public ResponseEntity<?> checkStatus(@RequestParam long accountId, @RequestParam long eventId) {
        boolean exists = registerService.checkRegistrationStatus(accountId, eventId);
        if (exists) {
            return ResponseEntity.ok("Registered");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not registered");
        }}

    @GetMapping("/event/{eventId}/checked-in")
    public ResponseEntity<List<RegisterResponse>> getCheckedInUsers(@PathVariable long eventId) {
        return ResponseEntity.ok(registerService.getCheckedInUsersByEventId(eventId));
    }

    // POST: Đăng ký tham gia
    @PostMapping
    public ResponseEntity<RegisterResponse> registerForEvent(@RequestBody RegisterRequest requestModel) {
        RegisterResponse createdRegister = registerService.registerForEvent(requestModel);
        return new ResponseEntity<>(createdRegister, HttpStatus.CREATED);
    }

    // PUT: Check-in
    @PutMapping("/check-in/{registerId}")
    public ResponseEntity<RegisterResponse> checkIn(@PathVariable Long registerId) {
        return ResponseEntity.ok(registerService.checkIn(registerId));
    }

    // DELETE: Hủy đăng ký (Unregister) - [MỚI]
    // URL: /api/register-event?accountId=1&eventId=2
    @DeleteMapping
    public ResponseEntity<String> cancelRegistration(@RequestParam long accountId, @RequestParam long eventId) {
        registerService.cancelRegistration(accountId, eventId);
        return ResponseEntity.ok("Cancelled registration successfully.");
    }
}
