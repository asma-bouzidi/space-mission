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
}
