package com.spacemission.model.user;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String email;
    private String fullName;
    private boolean enabled;
    private final Set<Role> roles;

    public User() {
        this.roles = new HashSet<>();
    }

    public User(int id, String username, String passwordHash, String email, String fullName) {
        this();
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullName = fullName;
        this.enabled = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        this.roles.add(Objects.requireNonNull(role, "Role cannot be null."));
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }
}
