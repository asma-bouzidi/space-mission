package com.spacemission.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MissionTeamRequest(
        @Min(value = 1, message = "Mission ID must be positive") int missionId,
        @Min(value = 1, message = "User ID must be positive") int userId,
        @NotBlank(message = "Role name is required") String roleName
) {
}
