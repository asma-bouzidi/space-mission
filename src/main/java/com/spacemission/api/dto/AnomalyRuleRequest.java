package com.spacemission.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AnomalyRuleRequest(
        @NotBlank(message = "Rule name is required") String name,
        @NotBlank(message = "Severity is required") String severity,
        boolean enabled,
        double thresholdValue,
        @NotBlank(message = "Metric name is required") String metricName,
        String description
) {
}
