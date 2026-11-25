package com.spacemission.api.security;

import com.spacemission.model.user.Role;
import com.spacemission.model.user.User;
import com.spacemission.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApiSecurityService {
    private final AuthService authService = new AuthService();
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public Session login(String username, String password) {
        User user = authService.login(username, password)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password."));

        String token = UUID.randomUUID().toString();
        Session session = new Session(token, user, Instant.now().plusSeconds(3600));
        sessions.put(token, session);
        return session;
    }

    public User requireUser(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        Session session = sessions.get(token);
        if (session == null || session.expiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired or invalid.");
        }
        return session.user();
    }

    public void requireAnyRole(String authorizationHeader, Role... requiredRoles) {
        User user = requireUser(authorizationHeader);
        Set<Role> allowed = Set.of(requiredRoles);
        boolean authorized = user.getRoles().stream().anyMatch(allowed::contains);
        if (!authorized) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions for this action.");
        }
    }

    public void requireRole(String authorizationHeader, Role role) {
        requireAnyRole(authorizationHeader, role);
    }

    public static String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is required.");
        }
        String trimmed = authorizationHeader.trim();
        if (!trimmed.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header must start with 'Bearer '.");
        }
        String token = trimmed.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bearer token is missing.");
        }
        return token;
    }

    public record Session(String token, User user, Instant expiresAt) {
    }
}
