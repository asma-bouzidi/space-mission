package com.spacemission.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResourceRequest(
        @NotBlank(message = "Resource name is required") String name,
        @NotBlank(message = "Resource type is required") String type,
        @NotNull(message = "Total quantity is required") Double totalQuantity,
        String unit,
        Double availableQuantity
) {
}
