package com.spacemission.model.spacecraft;

import com.spacemission.enums.SpacecraftType;

public class NavigationSatellite extends Spacecraft {
    private double navigationAccuracy;

    public NavigationSatellite(int id, String name, String serialNumber, String manufacturer) {
        super(id, name, serialNumber, SpacecraftType.NAVIGATION_SATELLITE, manufacturer);
        this.navigationAccuracy = 0.98;
    }

    public double getNavigationAccuracy() {
        return navigationAccuracy;
    }

    public void setNavigationAccuracy(double navigationAccuracy) {
        this.navigationAccuracy = navigationAccuracy;
    }

    @Override
    public String getSpecialty() {
        return "Orbit and navigation precision";
    }

    @Override
    public void simulate() {
        super.simulate();
        setVelocity(getVelocity() + 0.5);
    }
}
