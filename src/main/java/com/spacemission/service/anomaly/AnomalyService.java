package com.spacemission.service.anomaly;

import com.spacemission.dao.impl.JdbcAnomalyDao;
import com.spacemission.dao.interfaces.AnomalyDao;
import com.spacemission.model.anomaly.Anomaly;
import com.spacemission.model.anomaly.AnomalyRule;
import com.spacemission.strategy.anomaly.AnomalyDetectionStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AnomalyService {
    private final AnomalyDao anomalyDao;

    public AnomalyService() {
        this(new JdbcAnomalyDao());
    }

    public AnomalyService(AnomalyDao anomalyDao) {
        this.anomalyDao = Objects.requireNonNull(anomalyDao, "Anomaly DAO cannot be null.");
    }

    public Anomaly createAnomaly(Anomaly anomaly) {
        validate(anomaly);
        return anomalyDao.save(anomaly);
    }

    public Anomaly findById(long id) {
        return anomalyDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anomaly not found: " + id));
    }

    public List<Anomaly> findAll() {
        return anomalyDao.findAll();
    }

    public List<Anomaly> findOpenAnomalies() {
        return anomalyDao.findOpen();
    }

    public List<Anomaly> getAnomalies() {
        return findAll();
    }

    public void updateAnomaly(Anomaly anomaly) {
        validate(anomaly);
        if (anomaly.getId() <= 0) {
            throw new IllegalArgumentException("Anomaly id is required for update.");
        }
        anomalyDao.update(anomaly);
    }

    public void deleteAnomaly(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Anomaly id must be positive.");
        }
        anomalyDao.deleteById(id);
    }

    public Anomaly resolveAnomaly(long id, String resolutionNote) {
        Anomaly anomaly = findById(id);
        anomaly.setStatus("RESOLVED");
        anomaly.setResolvedAt(LocalDateTime.now());
        if (resolutionNote != null && !resolutionNote.isBlank()) {
            String text = anomaly.getDescription() == null || anomaly.getDescription().isBlank()
                    ? resolutionNote
                    : anomaly.getDescription() + "\nResolution: " + resolutionNote;
            anomaly.setDescription(text);
        }
        anomalyDao.update(anomaly);
        return anomaly;
    }

    public void detect(AnomalyDetectionStrategy strategy, double currentValue, double threshold, String title, String description) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null.");
        }
        if (strategy.detect(currentValue, threshold)) {
            createAnomaly(new Anomaly(0, 0, 0, 0, "WARNING", title, description, "OPEN"));
        }
    }

    public List<Anomaly> detectConfiguredRules(int spacecraftId, int missionId, Map<String, Double> metricValues) {
        if (metricValues == null || metricValues.isEmpty()) {
            throw new IllegalArgumentException("Metric values cannot be null or empty.");
        }

        List<Anomaly> created = new ArrayList<>();
        AnomalyRuleService ruleService = new AnomalyRuleService();

        for (AnomalyRule rule : ruleService.findEnabledRules()) {
            Double currentValue = metricValues.get(rule.getMetricName());
            if (currentValue == null) {
                continue;
            }

            boolean shouldTrigger = shouldTriggerForRule(rule, currentValue);
            if (!shouldTrigger) {
                continue;
            }

            Anomaly anomaly = new Anomaly(
                    0,
                    spacecraftId,
                    missionId,
                    rule.getId(),
                    rule.getSeverity(),
                    rule.getName(),
                    rule.getDescription() == null || rule.getDescription().isBlank()
                            ? "Detected by configured anomaly rule: " + rule.getName()
                            : rule.getDescription(),
                    "OPEN"
            );
            created.add(createAnomaly(anomaly));
        }

        return created;
    }

    private boolean shouldTriggerForRule(AnomalyRule rule, double currentValue) {
        if (rule == null) {
            return false;
        }

        String metricName = rule.getMetricName() == null ? "" : rule.getMetricName().toLowerCase();
        String ruleName = rule.getName() == null ? "" : rule.getName().toLowerCase();

        if (metricName.contains("battery") || metricName.contains("fuel") || metricName.contains("signal") || ruleName.contains("low") || ruleName.contains("critical")) {
            return currentValue <= rule.getThresholdValue();
        }

        if (metricName.contains("temperature") || metricName.contains("pressure") || metricName.contains("altitude") || ruleName.contains("high")) {
            return currentValue >= rule.getThresholdValue();
        }

        return currentValue >= rule.getThresholdValue();
    }

    private void validate(Anomaly anomaly) {
        if (anomaly == null) {
            throw new IllegalArgumentException("Anomaly cannot be null.");
        }
        if (anomaly.getTitle() == null || anomaly.getTitle().isBlank()) {
            throw new IllegalArgumentException("Anomaly title is required.");
        }
        if (anomaly.getSeverity() == null || anomaly.getSeverity().isBlank()) {
            throw new IllegalArgumentException("Anomaly severity is required.");
        }
    }
}
