package com.spacemission.api.controller;

import com.spacemission.api.dto.LoginRequest;
import com.spacemission.api.dto.LoginResponse;
import com.spacemission.api.dto.UserSummary;
import com.spacemission.api.security.ApiSecurityService;
import com.spacemission.model.user.Role;
import com.spacemission.model.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final ApiSecurityService securityService;

    public AuthController(ApiSecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        ApiSecurityService.Session session = securityService.login(request.username(), request.password());
        User user = session.user();
        List<String> roles = user.getRoles().stream().map(Role::name).sorted().toList();
        return ResponseEntity.ok(new LoginResponse(
                session.token(),
                new UserSummary(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.isEnabled(), roles),
                session.expiresAt(),
                roles
        ));
    }

    @GetMapping("/auth/me")
    public ResponseEntity<UserSummary> currentUser(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        User user = securityService.requireUser(authorizationHeader);
        return ResponseEntity.ok(toSummary(user));
    }

    private UserSummary toSummary(User user) {
        List<String> roles = user.getRoles().stream().map(Role::name).sorted().toList();
        return new UserSummary(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.isEnabled(), roles);
    }
}
