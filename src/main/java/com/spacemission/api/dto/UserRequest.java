package com.spacemission.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        @NotBlank(message = "Full name is required") String fullName,
        String password,
        boolean enabled,
        List<String> roles
) {
}
