package com.demo.demo.model.request;

import com.demo.demo.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotNull(message = "Vai trò không được để trống")
    private Role role;
}
