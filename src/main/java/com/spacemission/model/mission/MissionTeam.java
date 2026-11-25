package com.spacemission.model.mission;

import java.time.LocalDateTime;
import java.util.Objects;

public class MissionTeam {
    private int id;
    private int missionId;
    private int userId;
    private String roleName;
    private LocalDateTime assignedAt;

    public MissionTeam() {
        this.id = 0;
        this.missionId = 0;
        this.userId = 0;
        this.roleName = "MEMBER";
        this.assignedAt = LocalDateTime.now();
    }

    public MissionTeam(int id, int missionId, int userId, String roleName, LocalDateTime assignedAt) {
        this.id = id;
        this.missionId = missionId;
        this.userId = userId;
        this.roleName = roleName == null || roleName.isBlank() ? "MEMBER" : roleName.trim().toUpperCase();
        this.assignedAt = assignedAt == null ? LocalDateTime.now() : assignedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        if (missionId <= 0) {
            throw new IllegalArgumentException("Mission id must be positive.");
        }
        this.missionId = missionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User id must be positive.");
        }
        this.userId = userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null || roleName.isBlank() ? "MEMBER" : roleName.trim().toUpperCase();
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt == null ? LocalDateTime.now() : assignedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        MissionTeam that = (MissionTeam) object;
        return id == that.id && missionId == that.missionId && userId == that.userId && Objects.equals(roleName, that.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, missionId, userId, roleName);
    }
}
