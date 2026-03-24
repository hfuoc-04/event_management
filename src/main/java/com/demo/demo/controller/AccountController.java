package com.demo.demo.controller;

import com.demo.demo.entity.Account;
import com.demo.demo.model.reponse.AccountResponse;
import com.demo.demo.model.request.UpdateAccountRequest;
import com.demo.demo.model.request.UpdateFaceRequest;
import com.demo.demo.model.request.UpdateRoleRequest;
import com.demo.demo.service.AccountService;
import com.demo.demo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/accounts") // Sử dụng versioning cho API
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;
    /**
     * Endpoint để lấy tất cả các tài khoản.
     * Chỉ nên dành cho ADMIN.
     */
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<Account> accounts = accountService.getAll();
        // Chuyển đổi List<Account> thành List<AccountResponse> để không lộ thông tin nhạy cảm
        List<AccountResponse> accountResponses = accounts.stream()
                .map(account -> modelMapper.map(account, AccountResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(accountResponses);
    }

    /**
     * Endpoint để lấy thông tin một tài khoản theo ID.
     * ADMIN hoặc chính người dùng đó có thể gọi.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        Account account = accountService.getById(id);
        AccountResponse response = modelMapper.map(account, AccountResponse.class);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint để cập nhật thông tin cá nhân của tài khoản.
     * Thường là người dùng tự cập nhật thông tin của họ.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long id, @RequestBody UpdateAccountRequest request) {
        AccountResponse updatedAccount = accountService.update(id, request);
        return ResponseEntity.ok(updatedAccount);
    }

    /**
     * Endpoint để cập nhật vai trò (role) của một tài khoản.
     * Cực kỳ quan trọng: CHỈ DÀNH CHO ADMIN.
     */
    @PatchMapping("/{id}/role") // Dùng PATCH vì chỉ cập nhật một phần nhỏ
    public ResponseEntity<AccountResponse> updateUserRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        AccountResponse updatedAccount = accountService.updateRole(id, request);
        return ResponseEntity.ok(updatedAccount);
    }

    /**
     * Endpoint để xóa một tài khoản.
     * Chỉ dành cho ADMIN.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.delete(id);
        // Trả về status 204 No Content - là chuẩn cho việc xóa thành công
        return ResponseEntity.noContent().build();
    }

    /*
    LƯU Ý: Service của bạn có phương thức `create(Account account)`
    Tuy nhiên, việc truyền thẳng Entity `Account` từ client là không an toàn.
    Cách tốt hơn là tạo một DTO riêng cho việc tạo tài khoản.
    Dưới đây là một ví dụ về endpoint tạo tài khoản theo chuẩn.
    */
    /**
     * Endpoint để tạo một tài khoản mới (ví dụ bởi Admin).
     * Nếu là cho người dùng tự đăng ký, nên đặt ở một controller khác như AuthController.
     */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody Account request) {
        // Chuyển đổi từ Request DTO sang Entity
        Account newAccount = modelMapper.map(request, Account.class);

        // Gọi service để tạo
        Account createdAccount = accountService.create(newAccount);

        // Chuyển đổi từ Entity sang Response DTO để trả về
        AccountResponse response = modelMapper.map(createdAccount, AccountResponse.class);

        // Trả về status 201 Created cùng với thông tin tài khoản đã tạo
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/face")
    public ResponseEntity updateFace(@RequestBody UpdateFaceRequest updateFaceRequest){
        Account account = authenticationService.updateAndRegisterFace(updateFaceRequest);
        return ResponseEntity.ok(account);
    }
}
