package com.spacemission.config;

public final class DatabaseConfig {
    private static final String URL = readEnvOrDefault("SPACE_MISSION_DB_URL", "jdbc:mysql://localhost:3306/spacemission_db");
    private static final String USER = readEnvOrDefault("SPACE_MISSION_DB_USER", "spacemission_app");
    private static final String PASSWORD = readEnvOrDefault("SPACE_MISSION_DB_PASSWORD", "change_me");

    private DatabaseConfig() {
    }

    private static String readEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static String getUrl() {
        return URL;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }
}
