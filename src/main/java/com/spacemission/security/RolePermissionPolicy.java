package com.spacemission.security;

import com.spacemission.model.user.Role;
import com.spacemission.model.user.User;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public final class RolePermissionPolicy {
    private static final Map<Role, Set<String>> VIEW_PERMISSIONS = createViewPermissions();

    private RolePermissionPolicy() {
    }

    public static boolean canView(User user, String screen) {
        if (user == null) {
            return false;
        }
        if (user.hasRole(Role.ADMIN)) {
            return true;
        }
        for (Role role : user.getRoles()) {
            Set<String> allowed = VIEW_PERMISSIONS.get(role);
            if (allowed != null && allowed.contains(screen)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canWrite(User user, String operation) {
        if (user == null) {
            return false;
        }
        if (user.hasRole(Role.ADMIN)) {
            return true;
        }
        if (user.hasRole(Role.MISSION_CONTROL_OPERATOR)) {
            return Set.of("missions", "mission-planning", "spacecraft", "telemetry", "communications", "alerts").contains(operation);
        }
        if (user.hasRole(Role.ENGINEER)) {
            return Set.of("spacecraft", "telemetry", "resources", "alerts").contains(operation);
        }
        if (user.hasRole(Role.SCIENTIST)) {
            return Set.of("missions", "telemetry", "alerts").contains(operation);
        }
        return false;
    }

    private static Map<Role, Set<String>> createViewPermissions() {
        Map<Role, Set<String>> permissions = new EnumMap<>(Role.class);
        permissions.put(Role.MISSION_CONTROL_OPERATOR, Set.of(
                "Overview", "Missions", "Spacecraft", "Mission Planning", "Telemetry",
                "Anomalies & Alerts", "Resources", "Communications", "Reports & Analytics", "Settings"
        ));
        permissions.put(Role.ENGINEER, Set.of(
                "Overview", "Spacecraft", "Telemetry", "Anomalies & Alerts", "Resources", "Settings"
        ));
        permissions.put(Role.SCIENTIST, Set.of(
                "Overview", "Missions", "Telemetry", "Anomalies & Alerts", "Reports & Analytics", "Settings"
        ));
        permissions.put(Role.ADMIN, Collections.emptySet());
        return permissions;
    }
}
