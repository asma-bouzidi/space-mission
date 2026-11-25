package com.spacemission.model.spacecraft;

import com.spacemission.enums.SpacecraftType;

public class ObservationSatellite extends Spacecraft {
    private String cameraResolution;

    public ObservationSatellite(int id, String name, String serialNumber, String manufacturer) {
        super(id, name, serialNumber, SpacecraftType.OBSERVATION_SATELLITE, manufacturer);
        this.cameraResolution = "4K";
    }

    public String getCameraResolution() {
        return cameraResolution;
    }

    public void setCameraResolution(String cameraResolution) {
        this.cameraResolution = cameraResolution;
    }

    @Override
    public String getSpecialty() {
        return "Earth and deep-space imagery";
    }

    @Override
    public void simulate() {
        super.simulate();
        setTemperature(getTemperature() + 1.2);
    }
}
