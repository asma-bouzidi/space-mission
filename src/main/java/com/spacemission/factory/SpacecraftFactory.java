package com.spacemission.factory;

import com.spacemission.enums.SpacecraftType;
import com.spacemission.model.spacecraft.*;

public final class SpacecraftFactory {
    private SpacecraftFactory() {
    }

    public static Spacecraft create(SpacecraftType type, int id, String name, String serialNumber, String manufacturer) {
        if (type == null) {
            throw new IllegalArgumentException("Unsupported spacecraft type: null");
        }

        return switch (type) {
            case COMMUNICATION_SATELLITE -> new CommunicationSatellite(id, name, serialNumber, manufacturer);
            case OBSERVATION_SATELLITE -> new ObservationSatellite(id, name, serialNumber, manufacturer);
            case NAVIGATION_SATELLITE -> new NavigationSatellite(id, name, serialNumber, manufacturer);
            case SCIENTIFIC_SATELLITE -> new ScientificSatellite(id, name, serialNumber, manufacturer);
            case EXPLORATION_SPACECRAFT -> new ExplorationSpacecraft(id, name, serialNumber, manufacturer);
        };
    }
}
