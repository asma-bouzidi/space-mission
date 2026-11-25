package com.spacemission.model.spacecraft;

import com.spacemission.enums.SpacecraftType;

public class CommunicationSatellite extends Spacecraft {
    private int bandwidthCapacity;

    public CommunicationSatellite(int id, String name, String serialNumber, String manufacturer) {
        super(id, name, serialNumber, SpacecraftType.COMMUNICATION_SATELLITE, manufacturer);
        this.bandwidthCapacity = 100;
    }

    public int getBandwidthCapacity() {
        return bandwidthCapacity;
    }

    public void setBandwidthCapacity(int bandwidthCapacity) {
        this.bandwidthCapacity = bandwidthCapacity;
    }

    @Override
    public String getSpecialty() {
        return "Communication relay and signal management";
    }

    @Override
    public void simulate() {
        super.simulate();
        setCommunicationStatus("ONLINE");
        setBandwidthCapacity(Math.max(10, getBandwidthCapacity() - 1));
    }
}
