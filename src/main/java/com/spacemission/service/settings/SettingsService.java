package com.spacemission.service.settings;

import com.spacemission.model.settings.AppSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class SettingsService {
    private static final String SETTINGS_FILE = "app-settings.properties";

    public void save(AppSettings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("Settings cannot be null.");
        }

        Properties properties = new Properties();
        properties.setProperty("telemetryRateSeconds", String.valueOf(settings.getTelemetryRateSeconds()));
        properties.setProperty("satelliteSyncEnabled", String.valueOf(settings.isSatelliteSyncEnabled()));
        properties.setProperty("verbosityLevel", settings.getVerbosityLevel());
        properties.setProperty("autoAlertsEnabled", String.valueOf(settings.isAutoAlertsEnabled()));
        properties.setProperty("theme", settings.getTheme());

        try {
            Path path = Paths.get(SETTINGS_FILE);
            Files.createDirectories(path.toAbsolutePath().getParent() == null ? Paths.get(".") : path.toAbsolutePath().getParent());
            try (var output = Files.newOutputStream(path)) {
                properties.store(output, "SpaceMission application settings");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save application settings.", e);
        }
    }

    public AppSettings load() {
        AppSettings settings = new AppSettings();
        Path path = Paths.get(SETTINGS_FILE);
        if (!Files.exists(path)) {
            return settings;
        }

        Properties properties = new Properties();
        try (var input = Files.newInputStream(path)) {
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load application settings.", e);
        }

        settings.setTelemetryRateSeconds(readIntProperty(properties, "telemetryRateSeconds", 5));
        settings.setSatelliteSyncEnabled(readBooleanProperty(properties, "satelliteSyncEnabled", true));
        settings.setVerbosityLevel(readStringProperty(properties, "verbosityLevel", "Standard"));
        settings.setAutoAlertsEnabled(readBooleanProperty(properties, "autoAlertsEnabled", true));
        settings.setTheme(readStringProperty(properties, "theme", "midnight"));
        return settings;
    }

    private int readIntProperty(Properties properties, String key, int defaultValue) {
        String rawValue = properties.getProperty(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean readBooleanProperty(Properties properties, String key, boolean defaultValue) {
        String rawValue = properties.getProperty(key, String.valueOf(defaultValue));
        if (rawValue == null) {
            return defaultValue;
        }
        String normalized = rawValue.trim();
        if (normalized.equalsIgnoreCase("true") || normalized.equals("1") || normalized.equalsIgnoreCase("yes")) {
            return true;
        }
        if (normalized.equalsIgnoreCase("false") || normalized.equals("0") || normalized.equalsIgnoreCase("no")) {
            return false;
        }
        return defaultValue;
    }

    private String readStringProperty(Properties properties, String key, String defaultValue) {
        String value = properties.getProperty(key, defaultValue);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
