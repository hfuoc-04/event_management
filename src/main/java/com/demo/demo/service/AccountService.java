package com.demo.demo.service;

import com.demo.demo.entity.Account;
import com.demo.demo.entity.Gender;
import com.demo.demo.entity.Role;
import com.demo.demo.model.reponse.AccountResponse;
import com.demo.demo.model.request.UpdateAccountRequest;
import com.demo.demo.model.request.UpdateRoleRequest;
import com.demo.demo.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder; // <<<< THÊM DEPENDENCY
    private final ModelMapper modelMapper;

    public Account create(Account account) {
        return accountRepository.save(account);
    }

    public AccountResponse update(Long id, UpdateAccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // --- CÁC TRƯỜNG CŨ ---
        if (request.getFullName() != null) {
            account.setFullName(request.getFullName());
        }
        if (request.getGender() != null && !request.getGender().isEmpty()) {
            try {
                Gender genderEnum = Gender.valueOf(request.getGender().toUpperCase());
                account.setGender(genderEnum);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Giới tính không hợp lệ");
            }
        }
        if (request.getDateOfBirth() != null) {
            account.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getPhone() != null) {
            account.setPhone(request.getPhone());
        }

        // --- THÊM PHẦN NÀY ĐỂ UPDATE IMAGE ---
        // Giả sử trong Entity Account trường tên là userImage
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            account.setImage(request.getImage());
        }
        // -------------------------------------

        Account updatedAccount = accountRepository.save(account);
        return modelMapper.map(updatedAccount, AccountResponse.class);
    }

    @Transactional
    public AccountResponse updateRole(Long accountId, UpdateRoleRequest request) {
        // 1. Tìm tài khoản trong DB
        Account accountToUpdate = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản với ID: " + accountId));

        // 2. Logic nghiệp vụ an toàn: Ngăn chặn việc tạo ra một ADMIN khác
        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Không được phép gán vai trò ADMIN.");
        }

        // 3. Cập nhật vai trò và lưu lại
        accountToUpdate.setRole(request.getRole());
        Account updatedAccount = accountRepository.save(accountToUpdate);

        // 4. Trả về DTO
        return modelMapper.map(updatedAccount, AccountResponse.class);
    }
    public void delete(Long id) {
        accountRepository.deleteById(id);
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account getById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }
}
