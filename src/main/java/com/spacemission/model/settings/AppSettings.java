package com.spacemission.model.settings;

public class AppSettings {
    private int telemetryRateSeconds = 5;
    private boolean satelliteSyncEnabled = true;
    private String verbosityLevel = "Standard";
    private boolean autoAlertsEnabled = true;
    private String theme = "midnight";

    public int getTelemetryRateSeconds() {
        return telemetryRateSeconds;
    }

    public void setTelemetryRateSeconds(int telemetryRateSeconds) {
        this.telemetryRateSeconds = Math.max(1, telemetryRateSeconds);
    }

    public boolean isSatelliteSyncEnabled() {
        return satelliteSyncEnabled;
    }

    public void setSatelliteSyncEnabled(boolean satelliteSyncEnabled) {
        this.satelliteSyncEnabled = satelliteSyncEnabled;
    }

    public String getVerbosityLevel() {
        return verbosityLevel;
    }

    public void setVerbosityLevel(String verbosityLevel) {
        this.verbosityLevel = verbosityLevel == null || verbosityLevel.isBlank() ? "Standard" : verbosityLevel;
    }

    public boolean isAutoAlertsEnabled() {
        return autoAlertsEnabled;
    }

    public void setAutoAlertsEnabled(boolean autoAlertsEnabled) {
        this.autoAlertsEnabled = autoAlertsEnabled;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme == null || theme.isBlank() ? "midnight" : theme;
    }
}
