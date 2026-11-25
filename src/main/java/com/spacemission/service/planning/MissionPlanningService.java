package com.spacemission.service.planning;

import com.spacemission.dao.impl.JdbcMissionDao;
import com.spacemission.enums.MissionPriority;
import com.spacemission.enums.MissionStatus;
import com.spacemission.model.mission.Mission;
import com.spacemission.service.mission.MissionService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MissionPlanningService {
    private final MissionService missionService;

    public MissionPlanningService() {
        this(new MissionService(new JdbcMissionDao()));
    }

    public MissionPlanningService(MissionService missionService) {
        this.missionService = Objects.requireNonNull(missionService, "Mission service cannot be null.");
    }

    public Mission scheduleMission(String missionName, String targetOrbit, MissionPriority priority,
                                  LocalDateTime launchWindow, String objective, String crewStatus) {
        validateMissionName(missionName);
        validateOrbit(targetOrbit);
        Objects.requireNonNull(priority, "Mission priority cannot be null.");
        Objects.requireNonNull(launchWindow, "Launch window cannot be null.");

        String description = buildDescription(targetOrbit, objective, crewStatus);
        String missionCode = buildMissionCode(missionName, targetOrbit);
        Mission mission = new Mission(0, missionName, missionCode, description, priority, MissionStatus.PLANNED);
        mission.setMissionType(targetOrbit.toUpperCase(Locale.ROOT));
        mission.setStartDate(launchWindow);
        mission.setExpectedEndDate(launchWindow.plusHours(36));
        mission.setStatus(MissionStatus.READY);
        if (objective != null && !objective.isBlank()) {
            mission.addObjective(objective);
        }
        missionService.createMission(mission);
        return mission;
    }

    public Mission saveDraft(String missionName, String targetOrbit, MissionPriority priority,
                             LocalDateTime launchWindow, String objective, String crewStatus) {
        validateMissionName(missionName);
        validateOrbit(targetOrbit);
        Objects.requireNonNull(priority, "Mission priority cannot be null.");

        String description = buildDescription(targetOrbit, objective, crewStatus);
        String missionCode = buildMissionCode(missionName, targetOrbit);
        Mission mission = new Mission(0, missionName, missionCode, description, priority, MissionStatus.PLANNED);
        mission.setMissionType(targetOrbit.toUpperCase(Locale.ROOT));
        if (launchWindow != null) {
            mission.setStartDate(launchWindow);
            mission.setExpectedEndDate(launchWindow.plusHours(24));
        }
        if (objective != null && !objective.isBlank()) {
            mission.addObjective(objective);
        }
        missionService.createMission(mission);
        return mission;
    }

    public List<Mission> getRecentPlans(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return missionService.getAll().stream()
                .sorted(Comparator.comparingInt(Mission::getId).reversed())
                .limit(limit)
                .toList();
    }

    private void validateMissionName(String missionName) {
        if (missionName == null || missionName.isBlank()) {
            throw new IllegalArgumentException("Mission name is required.");
        }
    }

    private void validateOrbit(String targetOrbit) {
        if (targetOrbit == null || targetOrbit.isBlank()) {
            throw new IllegalArgumentException("Target orbit is required.");
        }
    }

    private String buildMissionCode(String missionName, String targetOrbit) {
        String base = missionName.trim().replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
        if (base.length() > 8) {
            base = base.substring(0, 8);
        }
        String orbit = targetOrbit.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
        long suffix = System.currentTimeMillis() % 1000000;
        return base + "-" + orbit + "-" + suffix;
    }

    private String buildDescription(String targetOrbit, String objective, String crewStatus) {
        StringBuilder description = new StringBuilder();
        description.append("Target orbit: ").append(targetOrbit.trim().toUpperCase(Locale.ROOT));
        if (objective != null && !objective.isBlank()) {
            description.append(" | Objective: ").append(objective.trim());
        }
        if (crewStatus != null && !crewStatus.isBlank()) {
            description.append(" | Crew status: ").append(crewStatus.trim());
        }
        return description.toString();
    }
}
