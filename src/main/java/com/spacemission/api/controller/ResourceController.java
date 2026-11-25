package com.spacemission.api.controller;

import com.spacemission.api.dto.ResourceRequest;
import com.spacemission.api.security.ApiSecurityService;
import com.spacemission.enums.ResourceType;
import com.spacemission.model.resource.Resource;
import com.spacemission.model.user.Role;
import com.spacemission.service.resource.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ResourceController {
    private final ResourceService resourceService = new ResourceService();
    private final ApiSecurityService securityService;

    public ResourceController(ApiSecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/resources")
    public ResponseEntity<List<Resource>> list(@RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR, Role.ENGINEER, Role.SCIENTIST);
        return ResponseEntity.ok(resourceService.findAll());
    }

    @GetMapping("/resources/{id}")
    public ResponseEntity<Resource> get(@PathVariable int id,
                                     @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR, Role.ENGINEER, Role.SCIENTIST);
        return ResponseEntity.ok(resourceService.findById(id));
    }

    @PostMapping("/resources")
    public ResponseEntity<Resource> create(@Valid @RequestBody ResourceRequest request,
                                         @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR);
        Resource resource = new Resource(0, request.name(), ResourceType.valueOf(request.type().trim().toUpperCase()),
                request.totalQuantity() == null ? 0 : request.totalQuantity(),
                request.unit() == null ? "units" : request.unit());
        if (request.availableQuantity() != null) {
            resource.setAvailableQuantity(request.availableQuantity());
        }
        Resource created = resourceService.createResource(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/resources/{id}")
    public ResponseEntity<Resource> update(@PathVariable int id,
                                         @Valid @RequestBody ResourceRequest request,
                                         @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR);
        Resource resource = resourceService.findById(id);
        resource.setName(request.name());
        resource.setAvailableQuantity(request.availableQuantity() == null ? resource.getAvailableQuantity() : request.availableQuantity());
        resourceService.updateResource(resource);
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/resources/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id,
                                     @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireRole(authorizationHeader, Role.ADMIN);
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
