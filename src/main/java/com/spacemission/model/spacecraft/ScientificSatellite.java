package com.spacemission.model.spacecraft;

import com.spacemission.enums.SpacecraftType;

public class ScientificSatellite extends Spacecraft {
    private String researchPayload;

    public ScientificSatellite(int id, String name, String serialNumber, String manufacturer) {
        super(id, name, serialNumber, SpacecraftType.SCIENTIFIC_SATELLITE, manufacturer);
        this.researchPayload = "Spectrometer Suite";
    }

    public String getResearchPayload() {
        return researchPayload;
    }

    public void setResearchPayload(String researchPayload) {
        this.researchPayload = researchPayload;
    }

    @Override
    public String getSpecialty() {
        return "Scientific observation and data collection";
    }

    @Override
    public void simulate() {
        super.simulate();
        setTemperature(getTemperature() + 2.5);
    }
}
