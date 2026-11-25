package com.spacemission.dao.interfaces;

import com.spacemission.model.telemetry.Telemetry;

import java.util.List;
import java.util.Optional;

public interface TelemetryDao {
    Telemetry save(Telemetry telemetry);

    Optional<Telemetry> findById(long id);

    List<Telemetry> findAll();

    List<Telemetry> findBySpacecraftId(int spacecraftId);

    void update(Telemetry telemetry);

    void deleteById(long id);
}
