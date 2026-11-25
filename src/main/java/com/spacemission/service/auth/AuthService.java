package com.spacemission.service.auth;

import com.spacemission.exception.AuthenticationException;
import com.spacemission.model.user.Role;
import com.spacemission.model.user.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
    private final Map<String, User> userStore = new ConcurrentHashMap<>();

    public AuthService() {
        User admin = new User(1, "admin", BCrypt.hashpw("admin", BCrypt.gensalt()), "admin@spacemission.local", "Administrator");
        admin.addRole(Role.ADMIN);
        admin.addRole(Role.MISSION_CONTROL_OPERATOR);
        userStore.put(normalizeUsername(admin.getUsername()), admin);

        User operator = new User(2, "operator", BCrypt.hashpw("admin", BCrypt.gensalt()), "operator@spacemission.local", "Mission Operator");
        operator.addRole(Role.MISSION_CONTROL_OPERATOR);
        userStore.put(normalizeUsername(operator.getUsername()), operator);

        User engineer = new User(3, "engineer", BCrypt.hashpw("admin", BCrypt.gensalt()), "engineer@spacemission.local", "Engineer");
        engineer.addRole(Role.ENGINEER);
        userStore.put(normalizeUsername(engineer.getUsername()), engineer);

        User scientist = new User(4, "scientist", BCrypt.hashpw("admin", BCrypt.gensalt()), "scientist@spacemission.local", "Scientist");
        scientist.addRole(Role.SCIENTIST);
        userStore.put(normalizeUsername(scientist.getUsername()), scientist);
    }

    public Optional<User> login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new AuthenticationException("Username and password are required.");
        }

        String normalizedUsername = normalizeUsername(username);
        User user = userStore.get(normalizedUsername);
        if (user == null || !user.isEnabled()) {
            return Optional.empty();
        }

        if (BCrypt.checkpw(password, user.getPasswordHash())) {
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public boolean hasAccess(User user, Role requiredRole) {
        return user != null && user.getRoles().contains(requiredRole);
    }

    public void registerUser(User user, String rawPassword) {
        if (user == null) {
            throw new AuthenticationException("User cannot be null.");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new AuthenticationException("Password is required.");
        }

        String normalizedUsername = normalizeUsername(user.getUsername());
        if (normalizedUsername.isBlank()) {
            throw new AuthenticationException("Username is required.");
        }
        if (userStore.containsKey(normalizedUsername)) {
            throw new AuthenticationException("Username already exists: " + normalizedUsername);
        }

        user.setUsername(normalizedUsername);
        user.setPasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
        userStore.put(normalizedUsername, user);
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }
}
