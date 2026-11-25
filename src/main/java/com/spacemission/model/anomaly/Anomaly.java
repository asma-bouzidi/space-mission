package com.spacemission.model.anomaly;

import java.time.LocalDateTime;

public class Anomaly {
    private int id;
    private int spacecraftId;
    private int missionId;
    private int ruleId;
    private String severity;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public Anomaly(int id, int spacecraftId, int missionId, int ruleId, String severity, String title, String description, String status) {
        this(id, spacecraftId, missionId, ruleId, severity, title, description, status, LocalDateTime.now(), null);
    }

    public Anomaly(int id, int spacecraftId, int missionId, int ruleId, String severity, String title, String description, String status, LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.spacecraftId = spacecraftId;
        this.missionId = missionId;
        this.ruleId = ruleId;
        this.severity = severity == null || severity.isBlank() ? "WARNING" : severity;
        this.title = title == null || title.isBlank() ? "Unnamed anomaly" : title;
        this.description = description == null ? "" : description;
        this.status = status == null || status.isBlank() ? "OPEN" : status;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.resolvedAt = resolvedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpacecraftId() {
        return spacecraftId;
    }

    public void setSpacecraftId(int spacecraftId) {
        this.spacecraftId = spacecraftId;
    }

    public int getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        this.missionId = missionId;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity == null || severity.isBlank() ? "WARNING" : severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null || title.isBlank() ? "Unnamed anomaly" : title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null || status.isBlank() ? "OPEN" : status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
