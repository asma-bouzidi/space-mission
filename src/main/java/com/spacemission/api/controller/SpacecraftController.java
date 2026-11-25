package com.spacemission.api.controller;

import com.spacemission.api.dto.SpacecraftRequest;
import com.spacemission.api.security.ApiSecurityService;
import com.spacemission.enums.SpacecraftStatus;
import com.spacemission.enums.SpacecraftType;
import com.spacemission.model.spacecraft.*;
import com.spacemission.service.spacecraft.SpacecraftService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SpacecraftController {
    private final SpacecraftService spacecraftService = new SpacecraftService(new com.spacemission.dao.impl.JdbcSpacecraftDao());
    private final ApiSecurityService securityService;

    public SpacecraftController(ApiSecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/spacecraft")
    public ResponseEntity<List<Spacecraft>> list(@RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, com.spacemission.model.user.Role.ADMIN,
                com.spacemission.model.user.Role.MISSION_CONTROL_OPERATOR,
                com.spacemission.model.user.Role.ENGINEER,
                com.spacemission.model.user.Role.SCIENTIST);
        return ResponseEntity.ok(spacecraftService.getAll());
    }

    @GetMapping("/spacecraft/{id}")
    public ResponseEntity<Spacecraft> get(@PathVariable int id,
                                        @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, com.spacemission.model.user.Role.ADMIN,
                com.spacemission.model.user.Role.MISSION_CONTROL_OPERATOR,
                com.spacemission.model.user.Role.ENGINEER,
                com.spacemission.model.user.Role.SCIENTIST);
        return ResponseEntity.ok(spacecraftService.findById(id));
    }

    @PostMapping("/spacecraft")
    public ResponseEntity<Spacecraft> create(@Valid @RequestBody SpacecraftRequest request,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, com.spacemission.model.user.Role.ADMIN, com.spacemission.model.user.Role.MISSION_CONTROL_OPERATOR);
        Spacecraft spacecraft = mapToEntity(request);
        spacecraftService.registerSpacecraft(spacecraft);
        return ResponseEntity.status(HttpStatus.CREATED).body(spacecraft);
    }

    @PutMapping("/spacecraft/{id}")
    public ResponseEntity<Spacecraft> update(@PathVariable int id,
                                            @Valid @RequestBody SpacecraftRequest request,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, com.spacemission.model.user.Role.ADMIN, com.spacemission.model.user.Role.MISSION_CONTROL_OPERATOR);
        Spacecraft spacecraft = mapToEntity(request);
        spacecraft.setId(id);
        spacecraftService.updateSpacecraft(spacecraft);
        return ResponseEntity.ok(spacecraft);
    }

    @DeleteMapping("/spacecraft/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id,
                                     @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireRole(authorizationHeader, com.spacemission.model.user.Role.ADMIN);
        spacecraftService.deleteSpacecraft(id);
        return ResponseEntity.noContent().build();
    }

    private Spacecraft mapToEntity(SpacecraftRequest request) {
        SpacecraftType type = SpacecraftType.valueOf(request.spacecraftType().trim().toUpperCase());
        Spacecraft spacecraft = switch (type) {
            case COMMUNICATION_SATELLITE -> new CommunicationSatellite(0, request.name(), request.serialNumber(), request.manufacturer() == null ? "Unknown" : request.manufacturer());
            case OBSERVATION_SATELLITE -> new ObservationSatellite(0, request.name(), request.serialNumber(), request.manufacturer() == null ? "Unknown" : request.manufacturer());
            case NAVIGATION_SATELLITE -> new NavigationSatellite(0, request.name(), request.serialNumber(), request.manufacturer() == null ? "Unknown" : request.manufacturer());
            case SCIENTIFIC_SATELLITE -> new ScientificSatellite(0, request.name(), request.serialNumber(), request.manufacturer() == null ? "Unknown" : request.manufacturer());
            case EXPLORATION_SPACECRAFT -> new ExplorationSpacecraft(0, request.name(), request.serialNumber(), request.manufacturer() == null ? "Unknown" : request.manufacturer());
        };
        spacecraft.setLaunchDate(request.launchDate());
        spacecraft.setStatus(SpacecraftStatus.valueOf(request.status().trim().toUpperCase()));
        spacecraft.setFuelLevel(request.fuelLevel());
        spacecraft.setBatteryLevel(request.batteryLevel());
        spacecraft.setAltitude(request.altitude());
        spacecraft.setVelocity(request.velocity());
        spacecraft.setOrbitType(request.orbitType() == null ? "LEO" : request.orbitType());
        spacecraft.setTemperature(request.temperature());
        spacecraft.setCommunicationStatus(request.communicationStatus() == null ? "ONLINE" : request.communicationStatus());
        return spacecraft;
    }
}
