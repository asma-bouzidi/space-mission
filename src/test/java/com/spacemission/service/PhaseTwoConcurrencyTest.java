package com.spacemission.service;

import com.spacemission.service.simulation.MissionSimulationService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PhaseTwoConcurrencyTest {

    @Test
    void shouldRunSimulationOnBackgroundThreadAndPublishTelemetry() throws InterruptedException {
        MissionSimulationService simulationService = new MissionSimulationService();
        CountDownLatch telemetryLatch = new CountDownLatch(1);
        AtomicReference<MissionSimulationService.SimulationSnapshot> telemetrySnapshot = new AtomicReference<>();

        simulationService.addTelemetryObserver(snapshot -> {
            telemetrySnapshot.set(snapshot);
            telemetryLatch.countDown();
        });

        simulationService.start();
        assertTrue(telemetryLatch.await(5, TimeUnit.SECONDS), "Telemetry observer should receive updates from the background simulation.");
        assertNotNull(telemetrySnapshot.get());
        assertTrue(telemetrySnapshot.get().getBatteryLevel() >= 50 && telemetrySnapshot.get().getBatteryLevel() <= 99);

        simulationService.stop();
    }

    @Test
    void shouldNotifyObserversWhenAnomalyThresholdIsReached() throws InterruptedException {
        MissionSimulationService simulationService = new MissionSimulationService();
        CountDownLatch anomalyLatch = new CountDownLatch(1);
        AtomicReference<String> anomalyText = new AtomicReference<>();

        simulationService.addAnomalyObserver((type, message) -> {
            anomalyText.set(type + ":" + message);
            anomalyLatch.countDown();
        });

        simulationService.start();
        assertTrue(anomalyLatch.await(8, TimeUnit.SECONDS), "Anomaly observer should receive a notification after the startup warm-up period.");
        assertNotNull(anomalyText.get());
        assertFalse(anomalyText.get().isBlank());

        simulationService.stop();
    }

    @Test
    void shouldNotTriggerCriticalAlertImmediatelyOnStartup() throws InterruptedException {
        MissionSimulationService simulationService = new MissionSimulationService();
        AtomicReference<String> anomalyText = new AtomicReference<>();

        simulationService.addAnomalyObserver((type, message) -> {
            anomalyText.set(type + ":" + message);
        });

        simulationService.start();
        Thread.sleep(1500);
        assertNull(anomalyText.get(), "Critical alerts should not fire immediately during the startup warm-up period.");

        simulationService.stop();
    }
}
