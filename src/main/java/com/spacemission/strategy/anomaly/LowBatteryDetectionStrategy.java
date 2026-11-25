package com.spacemission.strategy.anomaly;

public class LowBatteryDetectionStrategy implements AnomalyDetectionStrategy {
    @Override
    public boolean detect(double currentValue, double threshold) {
        return currentValue < threshold;
    }
}
