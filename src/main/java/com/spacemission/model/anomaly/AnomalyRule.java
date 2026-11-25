package com.spacemission.model.anomaly;

import java.util.Objects;

public class AnomalyRule {
    private int id;
    private String name;
    private String severity;
    private boolean enabled;
    private double thresholdValue;
    private String metricName;
    private String description;

    public AnomalyRule() {
        this(0, "", "WARNING", true, 0.0, "", "");
    }

    public AnomalyRule(int id, String name, String severity, boolean enabled, double thresholdValue, String metricName, String description) {
        this.id = id;
        this.name = name == null || name.isBlank() ? "Unnamed rule" : name.trim();
        this.severity = severity == null || severity.isBlank() ? "WARNING" : severity.trim().toUpperCase();
        this.enabled = enabled;
        this.thresholdValue = thresholdValue;
        this.metricName = metricName == null || metricName.isBlank() ? "unknown_metric" : metricName.trim();
        this.description = description == null ? "" : description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null || name.isBlank() ? "Unnamed rule" : name.trim();
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity == null || severity.isBlank() ? "WARNING" : severity.trim().toUpperCase();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName == null || metricName.isBlank() ? "unknown_metric" : metricName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        AnomalyRule that = (AnomalyRule) object;
        return id == that.id && enabled == that.enabled && Double.compare(that.thresholdValue, thresholdValue) == 0 && Objects.equals(name, that.name) && Objects.equals(severity, that.severity) && Objects.equals(metricName, that.metricName) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, severity, enabled, thresholdValue, metricName, description);
    }
}
