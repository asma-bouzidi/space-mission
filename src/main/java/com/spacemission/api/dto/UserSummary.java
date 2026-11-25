package com.spacemission.api.dto;

import java.util.List;

public record UserSummary(int id, String username, String email, String fullName, boolean enabled, List<String> roles) {
}
