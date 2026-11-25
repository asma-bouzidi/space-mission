package com.spacemission.service.dashboard;

import com.spacemission.dao.impl.JdbcMissionDao;
import com.spacemission.dao.impl.JdbcSpacecraftDao;
import com.spacemission.dao.impl.JdbcTelemetryDao;
import com.spacemission.enums.MissionStatus;
import com.spacemission.model.mission.Mission;
import com.spacemission.model.spacecraft.Spacecraft;
import com.spacemission.model.telemetry.Telemetry;
import com.spacemission.service.mission.MissionService;
import com.spacemission.service.spacecraft.SpacecraftService;
import com.spacemission.service.telemetry.TelemetryService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardOverviewService {
    private final MissionService missionService;
    private final SpacecraftService spacecraftService;
    private final TelemetryService telemetryService;

    public DashboardOverviewService() {
        this(new MissionService(new JdbcMissionDao()),
                new SpacecraftService(new JdbcSpacecraftDao()),
                new TelemetryService(new JdbcTelemetryDao()));
    }

    public DashboardOverviewService(MissionService missionService,
                                   SpacecraftService spacecraftService,
                                   TelemetryService telemetryService) {
        this.missionService = missionService;
        this.spacecraftService = spacecraftService;
        this.telemetryService = telemetryService;
    }

    public Map<String, String> getOverviewMetrics() {
        Map<String, String> values = new LinkedHashMap<>();
        List<Mission> missions = missionService.getAll();
        List<Spacecraft> spacecraft = spacecraftService.getAll();
        List<Telemetry> telemetry = telemetryService.findAll();

        int activeMissions = (int) missions.stream()
                .filter(m -> m.getStatus() == MissionStatus.READY
                        || m.getStatus() == MissionStatus.IN_PROGRESS
                        || m.getStatus() == MissionStatus.PAUSED
                        || m.getStatus() == MissionStatus.PREPARATION)
                .count();
        int completedMissions = (int) missions.stream()
                .filter(m -> m.getStatus() == MissionStatus.COMPLETED)
                .count();

        double meanBattery = telemetry.stream()
                .mapToDouble(Telemetry::getBatteryPercentage)
                .average()
                .orElse(0.0);

        values.put("totalSpacecraft", String.valueOf(spacecraft.size()));
        values.put("activeMissions", String.valueOf(activeMissions));
        values.put("completedMissions", String.valueOf(completedMissions));
        values.put("meanBattery", String.format("%.0f%%", meanBattery));
        return values;
    }
}
