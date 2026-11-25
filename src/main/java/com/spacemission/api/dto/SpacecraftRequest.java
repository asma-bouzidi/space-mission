package com.spacemission.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SpacecraftRequest(
        @NotBlank(message = "Spacecraft name is required") String name,
        @NotBlank(message = "Serial number is required") String serialNumber,
        @NotBlank(message = "Spacecraft type is required") String spacecraftType,
        String manufacturer,
        @NotNull(message = "Launch date is required") LocalDate launchDate,
        @NotBlank(message = "Status is required") String status,
        double fuelLevel,
        double batteryLevel,
        double altitude,
        double velocity,
        String orbitType,
        double temperature,
        String communicationStatus
) {
}
