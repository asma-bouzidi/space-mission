package com.spacemission.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MissionRequest(
        @NotBlank(message = "Mission name is required") String name,
        @NotBlank(message = "Mission code is required") String missionCode,
        String description,
        @NotBlank(message = "Mission type is required") String missionType,
        @NotBlank(message = "Priority is required") String priority,
        @NotNull(message = "Status is required") String status,
        LocalDateTime startDate,
        LocalDateTime expectedEndDate,
        LocalDateTime actualEndDate
) {
}
