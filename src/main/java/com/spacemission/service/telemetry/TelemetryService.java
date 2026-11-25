package com.spacemission.service.telemetry;

import com.spacemission.dao.impl.JdbcTelemetryDao;
import com.spacemission.dao.interfaces.TelemetryDao;
import com.spacemission.model.telemetry.Telemetry;

import java.util.List;
import java.util.Objects;

public class TelemetryService {
    private final TelemetryDao telemetryDao;

    public TelemetryService() {
        this(new JdbcTelemetryDao());
    }

    public TelemetryService(TelemetryDao telemetryDao) {
        this.telemetryDao = Objects.requireNonNull(telemetryDao, "Telemetry DAO cannot be null.");
    }

    public Telemetry createTelemetry(Telemetry telemetry) {
        validate(telemetry);
        return telemetryDao.save(telemetry);
    }

    public Telemetry findById(long id) {
        return telemetryDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Telemetry record not found: " + id));
    }

    public List<Telemetry> findAll() {
        return telemetryDao.findAll();
    }

    public List<Telemetry> findBySpacecraftId(int spacecraftId) {
        if (spacecraftId <= 0) {
            throw new IllegalArgumentException("Spacecraft id must be positive.");
        }
        return telemetryDao.findBySpacecraftId(spacecraftId);
    }

    public void updateTelemetry(Telemetry telemetry) {
        validate(telemetry);
        if (telemetry.getId() <= 0) {
            throw new IllegalArgumentException("Telemetry id is required for updates.");
        }
        telemetryDao.update(telemetry);
    }

    public void deleteTelemetry(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Telemetry id must be positive.");
        }
        telemetryDao.deleteById(id);
    }

    private void validate(Telemetry telemetry) {
        if (telemetry == null) {
            throw new IllegalArgumentException("Telemetry cannot be null.");
        }
        if (telemetry.getSpacecraftId() <= 0) {
            throw new IllegalArgumentException("Spacecraft id is required.");
        }
    }
}
