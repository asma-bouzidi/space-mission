package com.spacemission.model.spacecraft;

import com.spacemission.enums.SpacecraftType;

public class ExplorationSpacecraft extends Spacecraft {
    private double missionRange;

    public ExplorationSpacecraft(int id, String name, String serialNumber, String manufacturer) {
        super(id, name, serialNumber, SpacecraftType.EXPLORATION_SPACECRAFT, manufacturer);
        this.missionRange = 1500000.0;
    }

    public double getMissionRange() {
        return missionRange;
    }

    public void setMissionRange(double missionRange) {
        this.missionRange = missionRange;
    }

    @Override
    public String getSpecialty() {
        return "Deep-space exploration and autonomous missions";
    }

    @Override
    public void simulate() {
        super.simulate();
        setVelocity(getVelocity() + 2.3);
        setTemperature(getTemperature() + 3.1);
    }
}
