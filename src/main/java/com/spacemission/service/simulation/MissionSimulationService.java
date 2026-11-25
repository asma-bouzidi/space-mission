package com.spacemission.service.simulation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MissionSimulationService {
    private int batteryLevel = 86;
    private int fuelReserve = 72;
    private int signalStrength = 92;
    private int activeMissions = 12;
    private int criticalAlerts = 4;
    private int systemHealth = 97;

    private final List<TelemetryObserver> telemetryObservers = new CopyOnWriteArrayList<>();
    private final List<AnomalyObserver> anomalyObservers = new CopyOnWriteArrayList<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledExecutorService executorService;
    private Future<?> simulationTask;
    private int tickCount = 0;
    private static final int ANOMALY_STARTUP_WARMUP_TICKS = 3;

    public MissionSimulationService() {
        this.executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "mission-simulation-worker");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        simulationTask = executorService.scheduleAtFixedRate(() -> {
            try {
                SimulationSnapshot snapshot = nextSnapshot();
                notifyTelemetryObservers(snapshot);
                tickCount++;

                if (tickCount > ANOMALY_STARTUP_WARMUP_TICKS && shouldTriggerAnomaly(snapshot)) {
                    notifyAnomalyObservers("SYSTEM_ALERT", "Critical alert surge detected: " + snapshot.getCriticalAlerts() + " alerts active.");
                }
            } catch (Exception ex) {
                notifyAnomalyObservers("SIMULATION_ERROR", "Simulation loop failed: " + ex.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        running.set(false);
        if (simulationTask != null) {
            simulationTask.cancel(true);
        }
        executorService.shutdownNow();
        executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "mission-simulation-worker");
            thread.setDaemon(true);
            return thread;
        });
        simulationTask = null;
    }

    public void addTelemetryObserver(TelemetryObserver observer) {
        if (observer != null) {
            telemetryObservers.add(observer);
        }
    }

    public void removeTelemetryObserver(TelemetryObserver observer) {
        telemetryObservers.remove(observer);
    }

    public void addAnomalyObserver(AnomalyObserver observer) {
        if (observer != null) {
            anomalyObservers.add(observer);
        }
    }

    public void removeAnomalyObserver(AnomalyObserver observer) {
        anomalyObservers.remove(observer);
    }

    public SimulationSnapshot nextSnapshot() {
        batteryLevel = clamp(batteryLevel + ThreadLocalRandom.current().nextInt(-4, 5));
        fuelReserve = clamp(fuelReserve + ThreadLocalRandom.current().nextInt(-5, 6));
        signalStrength = clamp(signalStrength + ThreadLocalRandom.current().nextInt(-6, 7));
        activeMissions = clampRange(activeMissions + ThreadLocalRandom.current().nextInt(-2, 3), 9, 16);
        criticalAlerts = clampRange(criticalAlerts + ThreadLocalRandom.current().nextInt(-2, 3), 1, 8);
        systemHealth = clamp(systemHealth + ThreadLocalRandom.current().nextInt(-3, 4));

        return new SimulationSnapshot(batteryLevel, fuelReserve, signalStrength, activeMissions, criticalAlerts, systemHealth);
    }

    private boolean shouldTriggerAnomaly(SimulationSnapshot snapshot) {
        return snapshot.getCriticalAlerts() >= 3 || snapshot.getBatteryLevel() <= 60 || snapshot.getSignalStrength() <= 60;
    }

    private void notifyTelemetryObservers(SimulationSnapshot snapshot) {
        for (TelemetryObserver observer : telemetryObservers) {
            observer.onTelemetryUpdate(snapshot);
        }
    }

    private void notifyAnomalyObservers(String type, String message) {
        for (AnomalyObserver observer : anomalyObservers) {
            observer.onAnomaly(type, message);
        }
    }

    private int clamp(int value) {
        return Math.max(50, Math.min(99, value));
    }

    private int clampRange(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    @FunctionalInterface
    public interface TelemetryObserver {
        void onTelemetryUpdate(SimulationSnapshot snapshot);
    }

    @FunctionalInterface
    public interface AnomalyObserver {
        void onAnomaly(String type, String message);
    }

    public static class SimulationSnapshot {
        private final int batteryLevel;
        private final int fuelReserve;
        private final int signalStrength;
        private final int activeMissions;
        private final int criticalAlerts;
        private final int systemHealth;

        public SimulationSnapshot(int batteryLevel, int fuelReserve, int signalStrength,
                                 int activeMissions, int criticalAlerts, int systemHealth) {
            this.batteryLevel = batteryLevel;
            this.fuelReserve = fuelReserve;
            this.signalStrength = signalStrength;
            this.activeMissions = activeMissions;
            this.criticalAlerts = criticalAlerts;
            this.systemHealth = systemHealth;
        }

        public int getBatteryLevel() { return batteryLevel; }
        public int getFuelReserve() { return fuelReserve; }
        public int getSignalStrength() { return signalStrength; }
        public int getActiveMissions() { return activeMissions; }
        public int getCriticalAlerts() { return criticalAlerts; }
        public int getSystemHealth() { return systemHealth; }
    }
}
