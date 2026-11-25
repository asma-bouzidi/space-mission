package com.spacemission.api.controller;

import com.spacemission.api.dto.MissionRequest;
import com.spacemission.api.security.ApiSecurityService;
import com.spacemission.enums.MissionPriority;
import com.spacemission.enums.MissionStatus;
import com.spacemission.model.mission.Mission;
import com.spacemission.model.user.Role;
import com.spacemission.service.mission.MissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MissionController {
    private final MissionService missionService = new MissionService(new com.spacemission.dao.impl.JdbcMissionDao());
    private final ApiSecurityService securityService;

    public MissionController(ApiSecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/missions")
    public ResponseEntity<List<Mission>> list(@RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR, Role.ENGINEER, Role.SCIENTIST);
        return ResponseEntity.ok(missionService.getAll());
    }

    @GetMapping("/missions/{id}")
    public ResponseEntity<Mission> get(@PathVariable int id,
                                     @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR, Role.ENGINEER, Role.SCIENTIST);
        return ResponseEntity.ok(missionService.findById(id));
    }

    @PostMapping("/missions")
    public ResponseEntity<Mission> create(@Valid @RequestBody MissionRequest request,
                                         @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR);
        Mission mission = mapToEntity(request);
        missionService.createMission(mission);
        return ResponseEntity.status(HttpStatus.CREATED).body(mission);
    }

    @PutMapping("/missions/{id}")
    public ResponseEntity<Mission> update(@PathVariable int id,
                                         @Valid @RequestBody MissionRequest request,
                                         @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR);
        Mission mission = mapToEntity(request);
        mission.setId(id);
        missionService.updateMission(mission);
        return ResponseEntity.ok(mission);
    }

    @DeleteMapping("/missions/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id,
                                     @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireRole(authorizationHeader, Role.ADMIN);
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }

    private Mission mapToEntity(MissionRequest request) {
        Mission mission = new Mission(0, request.name(), request.missionCode(), request.description(),
                MissionPriority.valueOf(request.priority().trim().toUpperCase()),
                MissionStatus.valueOf(request.status().toString().trim().toUpperCase()));
        mission.setMissionType(request.missionType());
        mission.setStartDate(request.startDate());
        mission.setExpectedEndDate(request.expectedEndDate());
        mission.setActualEndDate(request.actualEndDate());
        return mission;
    }
}
