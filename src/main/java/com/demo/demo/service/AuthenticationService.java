package com.demo.demo.service;

import com.demo.demo.entity.Account;
import com.demo.demo.entity.Role;
import com.demo.demo.exception.exceptions.AuthenticationException;
import com.demo.demo.model.reponse.AccountResponse;
import com.demo.demo.model.reponse.RegisterAccountResponse;
import com.demo.demo.model.request.EmailDetailRequest;
import com.demo.demo.model.request.LoginRequest;
import com.demo.demo.model.request.RegisterAccountRequest;
import com.demo.demo.repository.AuthenticationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager; // giúp check đăng nhập

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TokenService tokenService;

    @Autowired
    EmailService emailService;



    public RegisterAccountResponse register(RegisterAccountRequest registerRequest) {
        // 1. Chuyển đổi từ RegisterAccountRequest DTO sang Account entity
        Account account = modelMapper.map(registerRequest, Account.class);

        // 2. Thiết lập các giá trị mặc định mà server quản lý
        account.setRole(Role.CUSTOMER); // Luôn set role mặc định là USER, không cho client tự chọn
        account.password = passwordEncoder.encode(registerRequest.getPassword());

        // 3. Lưu tài khoản mới vào DB
        Account newAccount = authenticationRepository.save(account);

        // 4. Gửi email (giữ nguyên)
        EmailDetailRequest emailDetail = new EmailDetailRequest();
        emailDetail.setRecipient(account.email);
        emailDetail.setSubject("Welcome to my system");
        emailService.sendMail(emailDetail);

        // 5. Chuyển đổi từ Account entity sang RegisterAccountResponse
        RegisterAccountResponse response = modelMapper.map(newAccount, RegisterAccountResponse.class);
        response.setMessage("Account created successfully for " + newAccount.getEmail());

        // 6. Trả về DTO thay vì entity
        return response;
    }

    public AccountResponse login(LoginRequest loginRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
        }catch (Exception e){
            e.printStackTrace();
            // sai thông tin đăng nhập
            System.out.println("Thông tin đăng nhập ko chính xác");

            throw new AuthenticationException("Invalid username or password");
        }

        Account account = authenticationRepository.findAccountByEmail(loginRequest.getEmail());
        AccountResponse accountResponse = modelMapper.map(account, AccountResponse.class);
        String token = tokenService.generateToken(account);
        accountResponse.setToken(token);
        return accountResponse;
    }

    public List<Account> getAllAccount(){
        List<Account> accounts = authenticationRepository.findAll();
        return accounts;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return authenticationRepository.findAccountByEmail(email);
    }
}
