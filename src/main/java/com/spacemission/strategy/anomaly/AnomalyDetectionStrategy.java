package com.spacemission.strategy.anomaly;

public interface AnomalyDetectionStrategy {
    boolean detect(double currentValue, double threshold);
}
