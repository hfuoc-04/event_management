package com.demo.demo.controller;

import com.demo.demo.entity.Account;
import com.demo.demo.model.reponse.AccountResponse;
import com.demo.demo.model.reponse.RegisterAccountResponse;
import com.demo.demo.model.request.LoginRequest;
import com.demo.demo.model.request.RegisterAccountRequest;
import com.demo.demo.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    // api > service > repository

    @PostMapping("/api/register")
    // Thay đổi Account thành RegisterAccountRequest
    // Thêm @Valid để các annotation validation trong DTO được kiểm tra
    public ResponseEntity<RegisterAccountResponse> register(@Valid @RequestBody RegisterAccountRequest registerRequest){
        RegisterAccountResponse response = authenticationService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/login")
    public ResponseEntity login( @RequestBody LoginRequest loginRequest){
        AccountResponse account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/api/accounts")
    public ResponseEntity getAllAccount(){
        List<Account> accounts = authenticationService.getAllAccount();
        return ResponseEntity.ok(accounts);
    }
}
