package com.spacemission.api.controller;

import com.spacemission.api.dto.UserRequest;
import com.spacemission.api.dto.UserSummary;
import com.spacemission.api.security.ApiSecurityService;
import com.spacemission.model.user.Role;
import com.spacemission.model.user.User;
import com.spacemission.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService = new UserService();
    private final ApiSecurityService securityService;

    public UserController(ApiSecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserSummary>> list(@RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR);
        return ResponseEntity.ok(userService.findAll().stream().map(this::toSummary).toList());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserSummary> get(@PathVariable int id,
                                         @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR);
        return ResponseEntity.ok(toSummary(userService.findById(id)));
    }

    @PostMapping("/users")
    public ResponseEntity<UserSummary> create(@Valid @RequestBody UserRequest request,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireRole(authorizationHeader, Role.ADMIN);
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setEnabled(request.enabled());
        if (request.roles() != null) {
            for (String roleName : request.roles()) {
                user.addRole(Role.valueOf(roleName.trim().toUpperCase()));
            }
        }
        User created = userService.createUser(user, request.password() == null ? "change_me" : request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(toSummary(created));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserSummary> update(@PathVariable int id,
                                             @Valid @RequestBody UserRequest request,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireRole(authorizationHeader, Role.ADMIN);
        User user = userService.findById(id);
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setEnabled(request.enabled());
        user.getRoles().clear();
        if (request.roles() != null) {
            for (String roleName : request.roles()) {
                user.addRole(Role.valueOf(roleName.trim().toUpperCase()));
            }
        }
        userService.updateUser(user);
        return ResponseEntity.ok(toSummary(user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id,
                                     @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireRole(authorizationHeader, Role.ADMIN);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private UserSummary toSummary(User user) {
        return new UserSummary(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.isEnabled(),
                user.getRoles().stream().map(Role::name).sorted().toList());
    }
}
