package com.spacemission;

import com.spacemission.config.DatabaseConfig;
import com.spacemission.model.Satellite;

public class Main {
    public static void main(String[] args) {
        System.out.println("SpaceMission Control starting...");
        System.out.println("Database URL: " + DatabaseConfig.getUrl());

        Satellite satellite = new Satellite(1, "Astra-01", "Low Earth Orbit", 2026);
        System.out.println("Sample satellite: " + satellite);
    }
}
