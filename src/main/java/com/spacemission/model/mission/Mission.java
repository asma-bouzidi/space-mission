package com.spacemission.model.mission;

import com.spacemission.enums.MissionPriority;
import com.spacemission.enums.MissionStatus;
import com.spacemission.exception.InvalidMissionStateException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Mission {
    private int id;
    private String name;
    private String missionCode;
    private String description;
    private String missionType;
    private LocalDateTime startDate;
    private LocalDateTime expectedEndDate;
    private LocalDateTime actualEndDate;
    private MissionPriority priority;
    private MissionStatus status;
    private final List<Integer> assignedSpacecraftIds;
    private final List<String> objectives;

    public Mission(int id, String name, String missionCode, String description,
                   MissionPriority priority, MissionStatus status) {
        if (id < 0) {
            throw new IllegalArgumentException("Mission id cannot be negative.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Mission name cannot be empty.");
        }
        if (missionCode == null || missionCode.isBlank()) {
            throw new IllegalArgumentException("Mission code cannot be empty.");
        }

        this.id = id;
        this.name = name;
        this.missionCode = missionCode;
        this.description = description == null ? "" : description;
        this.missionType = "GENERAL";
        this.priority = Objects.requireNonNullElse(priority, MissionPriority.MEDIUM);
        this.status = Objects.requireNonNullElse(status, MissionStatus.PLANNED);
        this.assignedSpacecraftIds = new ArrayList<>();
        this.objectives = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Mission id cannot be negative.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Mission name cannot be empty.");
        }
        this.name = name;
    }

    public String getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(String missionCode) {
        if (missionCode == null || missionCode.isBlank()) {
            throw new IllegalArgumentException("Mission code cannot be empty.");
        }
        this.missionCode = missionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public String getMissionType() {
        return missionType;
    }

    public void setMissionType(String missionType) {
        this.missionType = missionType == null ? "GENERAL" : missionType;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(LocalDateTime expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public LocalDateTime getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDateTime actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public MissionPriority getPriority() {
        return priority;
    }

    public void setPriority(MissionPriority priority) {
        this.priority = priority == null ? MissionPriority.MEDIUM : priority;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = Objects.requireNonNullElse(status, MissionStatus.PLANNED);
    }

    public List<Integer> getAssignedSpacecraftIds() {
        return Collections.unmodifiableList(assignedSpacecraftIds);
    }

    public void assignSpacecraft(int spacecraftId) {
        if (spacecraftId <= 0) {
            throw new IllegalArgumentException("Spacecraft id must be positive.");
        }
        if (!assignedSpacecraftIds.contains(spacecraftId)) {
            assignedSpacecraftIds.add(spacecraftId);
        }
    }

    public List<String> getObjectives() {
        return Collections.unmodifiableList(objectives);
    }

    public void addObjective(String objective) {
        if (objective == null || objective.isBlank()) {
            throw new IllegalArgumentException("Objective cannot be empty.");
        }
        objectives.add(objective);
    }

    public boolean canTransitionTo(MissionStatus nextStatus) {
        if (nextStatus == null) {
            return false;
        }

        return switch (status) {
            case PLANNED -> nextStatus == MissionStatus.PREPARATION || nextStatus == MissionStatus.READY || nextStatus == MissionStatus.ABORTED;
            case PREPARATION -> nextStatus == MissionStatus.READY || nextStatus == MissionStatus.FAILED || nextStatus == MissionStatus.ABORTED;
            case READY -> nextStatus == MissionStatus.IN_PROGRESS || nextStatus == MissionStatus.FAILED || nextStatus == MissionStatus.ABORTED;
            case IN_PROGRESS -> nextStatus == MissionStatus.PAUSED || nextStatus == MissionStatus.COMPLETED || nextStatus == MissionStatus.FAILED || nextStatus == MissionStatus.ABORTED;
            case PAUSED -> nextStatus == MissionStatus.READY || nextStatus == MissionStatus.IN_PROGRESS || nextStatus == MissionStatus.ABORTED;
            case COMPLETED, FAILED, ABORTED -> false;
        };
    }

    public void updateStatus(MissionStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null.");
        }
        if (!canTransitionTo(newStatus)) {
            throw new InvalidMissionStateException("Invalid mission state transition from " + status + " to " + newStatus + ".");
        }

        this.status = newStatus;
        if (newStatus == MissionStatus.COMPLETED) {
            this.actualEndDate = LocalDateTime.now();
        }
    }
}
