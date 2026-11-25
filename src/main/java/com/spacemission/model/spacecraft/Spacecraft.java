package com.spacemission.model.spacecraft;

import com.spacemission.enums.SpacecraftStatus;
import com.spacemission.enums.SpacecraftType;
import com.spacemission.interfaces.Communicable;
import com.spacemission.interfaces.Monitorable;
import com.spacemission.interfaces.Reportable;
import com.spacemission.interfaces.Simulatable;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Spacecraft implements Communicable, Monitorable, Simulatable, Reportable {
    private int id;
    private String name;
    private String serialNumber;
    private final SpacecraftType spacecraftType;
    private String manufacturer;
    private LocalDate launchDate;
    private SpacecraftStatus status;
    private double fuelLevel;
    private double batteryLevel;
    private double altitude;
    private double velocity;
    private String orbitType;
    private double temperature;
    private String communicationStatus;

    protected Spacecraft(int id, String name, String serialNumber, SpacecraftType spacecraftType, String manufacturer) {
        if (id < 0) {
            throw new IllegalArgumentException("Spacecraft id cannot be negative.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Spacecraft name cannot be empty.");
        }
        if (serialNumber == null || serialNumber.isBlank()) {
            throw new IllegalArgumentException("Serial number cannot be empty.");
        }
        if (spacecraftType == null) {
            throw new IllegalArgumentException("Spacecraft type cannot be null.");
        }

        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.spacecraftType = spacecraftType;
        this.manufacturer = Objects.requireNonNullElse(manufacturer, "Unknown manufacturer");
        this.launchDate = LocalDate.now();
        this.status = SpacecraftStatus.ACTIVE;
        this.fuelLevel = 100.0;
        this.batteryLevel = 100.0;
        this.altitude = 0.0;
        this.velocity = 0.0;
        this.orbitType = "LEO";
        this.temperature = 20.0;
        this.communicationStatus = "ONLINE";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Spacecraft id cannot be negative.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Spacecraft name cannot be empty.");
        }
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        if (serialNumber == null || serialNumber.isBlank()) {
            throw new IllegalArgumentException("Serial number cannot be empty.");
        }
        this.serialNumber = serialNumber;
    }

    public SpacecraftType getSpacecraftType() {
        return spacecraftType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = Objects.requireNonNullElse(manufacturer, "Unknown manufacturer");
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    public SpacecraftStatus getStatus() {
        return status;
    }

    public void setStatus(SpacecraftStatus status) {
        this.status = Objects.requireNonNull(status, "Spacecraft status cannot be null.");
    }

    public double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(double fuelLevel) {
        if (fuelLevel < 0 || fuelLevel > 100) {
            throw new IllegalArgumentException("Fuel level must be between 0 and 100.");
        }
        this.fuelLevel = fuelLevel;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        if (batteryLevel < 0 || batteryLevel > 100) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100.");
        }
        this.batteryLevel = batteryLevel;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public String getOrbitType() {
        return orbitType;
    }

    public void setOrbitType(String orbitType) {
        this.orbitType = orbitType == null ? "LEO" : orbitType;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getCommunicationStatus() {
        return communicationStatus;
    }

    public void setCommunicationStatus(String communicationStatus) {
        this.communicationStatus = communicationStatus == null ? "ONLINE" : communicationStatus;
    }

    public abstract String getSpecialty();

    @Override
    public String sendCommand(String command) {
        return "Command sent to " + name + ": " + command;
    }

    @Override
    public double getTelemetryValue(String metricName) {
        if (metricName == null) {
            return 0.0;
        }

        return switch (metricName.toLowerCase()) {
            case "battery" -> batteryLevel;
            case "fuel" -> fuelLevel;
            case "altitude" -> altitude;
            case "velocity" -> velocity;
            case "temperature" -> temperature;
            default -> 0.0;
        };
    }

    @Override
    public void simulate() {
        batteryLevel = Math.max(0, batteryLevel - 0.5);
        fuelLevel = Math.max(0, fuelLevel - 0.2);
        temperature = Math.max(0, temperature + 0.4);
        altitude = altitude + 5.0;
        velocity = velocity + 1.2;
    }

    @Override
    public String generateReport() {
        return "Spacecraft report for " + name + " (" + spacecraftType + ")" +
                " | Fuel=" + fuelLevel + "% | Battery=" + batteryLevel + "% | Altitude=" + altitude + " km";
    }

    @Override
    public String toString() {
        return name + " [" + spacecraftType + "]";
    }
}
