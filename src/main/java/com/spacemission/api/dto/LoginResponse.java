package com.spacemission.api.dto;

import java.time.Instant;
import java.util.List;

public record LoginResponse(String token, UserSummary user, Instant expiresAt, List<String> roles) {
}
