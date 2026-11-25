package com.spacemission.api.controller;

import com.spacemission.api.dto.AnomalyRuleRequest;
import com.spacemission.api.security.ApiSecurityService;
import com.spacemission.model.anomaly.AnomalyRule;
import com.spacemission.model.user.Role;
import com.spacemission.service.anomaly.AnomalyRuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AnomalyRuleController {
    private final AnomalyRuleService anomalyRuleService = new AnomalyRuleService();
    private final ApiSecurityService securityService;

    public AnomalyRuleController(ApiSecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/anomaly-rules")
    public ResponseEntity<List<AnomalyRule>> list(@RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR, Role.ENGINEER, Role.SCIENTIST);
        return ResponseEntity.ok(anomalyRuleService.findAll());
    }

    @GetMapping("/anomaly-rules/{id}")
    public ResponseEntity<AnomalyRule> get(@PathVariable int id,
                                         @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.MISSION_CONTROL_OPERATOR, Role.ENGINEER, Role.SCIENTIST);
        return ResponseEntity.ok(anomalyRuleService.findById(id));
    }

    @PostMapping("/anomaly-rules")
    public ResponseEntity<AnomalyRule> create(@Valid @RequestBody AnomalyRuleRequest request,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.ENGINEER);
        AnomalyRule rule = new AnomalyRule();
        rule.setName(request.name());
        rule.setSeverity(request.severity());
        rule.setEnabled(request.enabled());
        rule.setThresholdValue(request.thresholdValue());
        rule.setMetricName(request.metricName());
        rule.setDescription(request.description());
        AnomalyRule created = anomalyRuleService.create(rule);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/anomaly-rules/{id}")
    public ResponseEntity<AnomalyRule> update(@PathVariable int id,
                                            @Valid @RequestBody AnomalyRuleRequest request,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireAnyRole(authorizationHeader, Role.ADMIN, Role.ENGINEER);
        AnomalyRule rule = anomalyRuleService.findById(id);
        rule.setName(request.name());
        rule.setSeverity(request.severity());
        rule.setEnabled(request.enabled());
        rule.setThresholdValue(request.thresholdValue());
        rule.setMetricName(request.metricName());
        rule.setDescription(request.description());
        anomalyRuleService.update(rule);
        return ResponseEntity.ok(rule);
    }

    @DeleteMapping("/anomaly-rules/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id,
                                     @RequestHeader("Authorization") String authorizationHeader) {
        securityService.requireRole(authorizationHeader, Role.ADMIN);
        anomalyRuleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
