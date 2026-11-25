package com.spacemission.api.controller;

import com.spacemission.api.dto.MissionTeamRequest;
import com.spacemission.api.security.ApiSecurityService;
import com.spacemission.model.mission.MissionTeam;
import com.spacemission.model.user.Role;
import com.spacemission.service.mission.MissionTeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MissionTeamController {
    private final MissionTeamService missionTeamService = new MissionTeamService();
    private final ApiSecurityService securityService;

    public MissionTeamController(ApiSecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/mission-teams")
    public ResponseEntity<List<MissionTeam>> list(@RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR, Role.ENGINEER, Role.SCIENTIST);
        return ResponseEntity.ok(missionTeamService.findAll());
    }

    @GetMapping("/mission-teams/{id}")
    public ResponseEntity<MissionTeam> get(@PathVariable int id,
                                         @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR, Role.ENGINEER, Role.SCIENTIST);
        return ResponseEntity.ok(missionTeamService.findById(id));
    }

    @PostMapping("/mission-teams")
    public ResponseEntity<MissionTeam> create(@Valid @RequestBody MissionTeamRequest request,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR);
        MissionTeam team = new MissionTeam();
        team.setMissionId(request.missionId());
        team.setUserId(request.userId());
        team.setRoleName(request.roleName());
        MissionTeam created = missionTeamService.create(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/mission-teams/{id}")
    public ResponseEntity<MissionTeam> update(@PathVariable int id,
                                            @Valid @RequestBody MissionTeamRequest request,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR);
        MissionTeam team = missionTeamService.findById(id);
        team.setMissionId(request.missionId());
        team.setUserId(request.userId());
        team.setRoleName(request.roleName());
        missionTeamService.update(team);
        return ResponseEntity.ok(team);
    }

    @DeleteMapping("/mission-teams/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id,
                                     @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR);
        missionTeamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
