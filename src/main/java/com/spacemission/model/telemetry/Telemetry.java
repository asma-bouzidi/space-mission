package com.spacemission.model.telemetry;

import java.time.LocalDateTime;

public class Telemetry {
    private int id;
    private int spacecraftId;
    private double temperature;
    private double voltage;
    private double batteryPercentage;
    private double fuelPercentage;
    private double altitude;
    private double velocity;
    private double pressure;
    private double signalStrength;
    private double solarPanelEfficiency;
    private double communicationQuality;
    private LocalDateTime capturedAt;

    public Telemetry(int id, int spacecraftId, double temperature, double voltage, double batteryPercentage,
                     double fuelPercentage, double altitude, double velocity, double pressure,
                     double signalStrength, double solarPanelEfficiency, double communicationQuality) {
        this(id, spacecraftId, temperature, voltage, batteryPercentage, fuelPercentage, altitude, velocity,
                pressure, signalStrength, solarPanelEfficiency, communicationQuality, LocalDateTime.now());
    }

    public Telemetry(int id, int spacecraftId, double temperature, double voltage, double batteryPercentage,
                     double fuelPercentage, double altitude, double velocity, double pressure,
                     double signalStrength, double solarPanelEfficiency, double communicationQuality,
                     LocalDateTime capturedAt) {
        this.id = id;
        this.spacecraftId = spacecraftId;
        this.temperature = temperature;
        this.voltage = voltage;
        this.batteryPercentage = batteryPercentage;
        this.fuelPercentage = fuelPercentage;
        this.altitude = altitude;
        this.velocity = velocity;
        this.pressure = pressure;
        this.signalStrength = signalStrength;
        this.solarPanelEfficiency = solarPanelEfficiency;
        this.communicationQuality = communicationQuality;
        this.capturedAt = capturedAt == null ? LocalDateTime.now() : capturedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpacecraftId() {
        return spacecraftId;
    }

    public void setSpacecraftId(int spacecraftId) {
        this.spacecraftId = spacecraftId;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(double batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public double getFuelPercentage() {
        return fuelPercentage;
    }

    public void setFuelPercentage(double fuelPercentage) {
        this.fuelPercentage = fuelPercentage;
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

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(double signalStrength) {
        this.signalStrength = signalStrength;
    }

    public double getSolarPanelEfficiency() {
        return solarPanelEfficiency;
    }

    public void setSolarPanelEfficiency(double solarPanelEfficiency) {
        this.solarPanelEfficiency = solarPanelEfficiency;
    }

    public double getCommunicationQuality() {
        return communicationQuality;
    }

    public void setCommunicationQuality(double communicationQuality) {
        this.communicationQuality = communicationQuality;
    }

    public LocalDateTime getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(LocalDateTime capturedAt) {
        this.capturedAt = capturedAt == null ? LocalDateTime.now() : capturedAt;
    }
}
