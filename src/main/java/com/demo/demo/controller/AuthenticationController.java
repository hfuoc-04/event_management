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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth") // Gom nhóm chuẩn hóa đường dẫn cho phần Auth
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    // Đường dẫn thực tế sẽ là: POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<RegisterAccountResponse> register(@Valid @RequestBody RegisterAccountRequest registerRequest){
        RegisterAccountResponse response = authenticationService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    // Đường dẫn thực tế sẽ là: POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<AccountResponse> login(@RequestBody LoginRequest loginRequest){
        AccountResponse account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }

//    @GetMapping("/api/accounts")
//    public ResponseEntity getAllAccount(){
//        List<Account> accounts = authenticationService.getAllAccount();
//        return ResponseEntity.ok(accounts);
//    }
}
