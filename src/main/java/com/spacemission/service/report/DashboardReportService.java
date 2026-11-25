package com.spacemission.service.report;

import com.spacemission.dao.impl.JdbcMissionDao;
import com.spacemission.enums.MissionPriority;
import com.spacemission.enums.MissionStatus;
import com.spacemission.model.mission.Mission;
import com.spacemission.service.mission.MissionService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardReportService {
    private final MissionService missionService;

    public DashboardReportService() {
        this(new MissionService(new JdbcMissionDao()));
    }

    public DashboardReportService(MissionService missionService) {
        this.missionService = missionService;
    }

    public Map<String, Integer> getMissionMetrics() {
        List<Mission> missions = missionService.getAll();
        Map<String, Integer> metrics = new LinkedHashMap<>();

        int total = missions.size();
        int active = (int) missions.stream().filter(m -> m.getStatus() == MissionStatus.READY || m.getStatus() == MissionStatus.IN_PROGRESS || m.getStatus() == MissionStatus.PAUSED || m.getStatus() == MissionStatus.PREPARATION).count();
        int completed = (int) missions.stream().filter(m -> m.getStatus() == MissionStatus.COMPLETED).count();
        int critical = (int) missions.stream().filter(m -> m.getPriority() == MissionPriority.CRITICAL).count();

        metrics.put("totalMissions", total);
        metrics.put("activeMissions", active);
        metrics.put("completedMissions", completed);
        metrics.put("criticalMissions", critical);
        return metrics;
    }

    public List<String[]> buildMissionSummaryRows() {
        return missionService.getAll().stream()
                .map(mission -> new String[]{
                        mission.getName(),
                        mission.getPriority().name(),
                        mission.getStatus().name(),
                        mission.getMissionType(),
                        mission.getAssignedSpacecraftIds().isEmpty() ? "Unassigned" : mission.getAssignedSpacecraftIds().toString()
                })
                .toList();
    }
}
