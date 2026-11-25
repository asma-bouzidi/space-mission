package com.spacemission.service.spacecraft;

import com.spacemission.dao.impl.JdbcSpacecraftDao;
import com.spacemission.dao.interfaces.SpacecraftDao;
import com.spacemission.enums.SpacecraftStatus;
import com.spacemission.model.spacecraft.Spacecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpacecraftService {
    private final SpacecraftDao spacecraftDao;
    private final List<Spacecraft> spacecraftList = new ArrayList<>();

    public SpacecraftService() {
        this(null);
    }

    public SpacecraftService(SpacecraftDao spacecraftDao) {
        this.spacecraftDao = spacecraftDao;
    }

    public void registerSpacecraft(Spacecraft spacecraft) {
        if (spacecraft == null) {
            throw new IllegalArgumentException("Spacecraft cannot be null.");
        }
        if (spacecraft.getFuelLevel() < 10) {
            throw new IllegalArgumentException("Fuel level too low for registration.");
        }
        if (spacecraftDao != null) {
            spacecraftDao.save(spacecraft);
            spacecraftList.clear();
            spacecraftList.addAll(spacecraftDao.findAll());
            return;
        }
        spacecraftList.add(spacecraft);
    }

    public void updateSpacecraft(Spacecraft spacecraft) {
        if (spacecraft == null) {
            throw new IllegalArgumentException("Spacecraft cannot be null.");
        }
        if (spacecraftDao != null) {
            spacecraftDao.update(spacecraft);
            spacecraftList.clear();
            spacecraftList.addAll(spacecraftDao.findAll());
            return;
        }
        Spacecraft existing = findById(spacecraft.getId());
        existing.setName(spacecraft.getName());
        existing.setSerialNumber(spacecraft.getSerialNumber());
        existing.setManufacturer(spacecraft.getManufacturer());
        existing.setLaunchDate(spacecraft.getLaunchDate());
        existing.setStatus(spacecraft.getStatus());
        existing.setFuelLevel(spacecraft.getFuelLevel());
        existing.setBatteryLevel(spacecraft.getBatteryLevel());
        existing.setAltitude(spacecraft.getAltitude());
        existing.setVelocity(spacecraft.getVelocity());
        existing.setOrbitType(spacecraft.getOrbitType());
        existing.setTemperature(spacecraft.getTemperature());
        existing.setCommunicationStatus(spacecraft.getCommunicationStatus());
    }

    public void deleteSpacecraft(int spacecraftId) {
        if (spacecraftDao != null) {
            spacecraftDao.deleteById(spacecraftId);
            spacecraftList.clear();
            spacecraftList.addAll(spacecraftDao.findAll());
            return;
        }
        Spacecraft spacecraft = findById(spacecraftId);
        spacecraftList.remove(spacecraft);
    }

    public void updateStatus(int spacecraftId, SpacecraftStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Spacecraft status cannot be null.");
        }
        Spacecraft spacecraft = findById(spacecraftId);
        spacecraft.setStatus(newStatus);
        if (spacecraftDao != null) {
            spacecraftDao.update(spacecraft);
            spacecraftList.clear();
            spacecraftList.addAll(spacecraftDao.findAll());
        }
    }

    public Spacecraft findById(int spacecraftId) {
        if (spacecraftDao != null) {
            return spacecraftDao.findById(spacecraftId)
                    .orElseThrow(() -> new IllegalArgumentException("Spacecraft not found: " + spacecraftId));
        }
        return spacecraftList.stream()
                .filter(spacecraft -> spacecraft.getId() == spacecraftId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Spacecraft not found: " + spacecraftId));
    }

    public List<Spacecraft> findByStatus(SpacecraftStatus status) {
        Objects.requireNonNull(status, "Status cannot be null.");
        if (spacecraftDao != null) {
            return spacecraftDao.findByStatus(status.name());
        }
        return spacecraftList.stream()
                .filter(spacecraft -> spacecraft.getStatus() == status)
                .toList();
    }

    public List<Spacecraft> getAll() {
        if (spacecraftDao != null) {
            return spacecraftDao.findAll();
        }
        return new ArrayList<>(spacecraftList);
    }
}
