package com.spacemission.service.anomaly;

import com.spacemission.dao.impl.JdbcAnomalyRuleDao;
import com.spacemission.dao.interfaces.AnomalyRuleDao;
import com.spacemission.model.anomaly.AnomalyRule;

import java.util.List;
import java.util.Objects;

public class AnomalyRuleService {
    private final AnomalyRuleDao anomalyRuleDao;

    public AnomalyRuleService() {
        this(new JdbcAnomalyRuleDao());
    }

    public AnomalyRuleService(AnomalyRuleDao anomalyRuleDao) {
        this.anomalyRuleDao = Objects.requireNonNull(anomalyRuleDao, "Anomaly rule DAO cannot be null.");
    }

    public AnomalyRule create(AnomalyRule anomalyRule) {
        validate(anomalyRule);
        return anomalyRuleDao.save(anomalyRule);
    }

    public AnomalyRule findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Anomaly rule id must be positive.");
        }
        return anomalyRuleDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anomaly rule not found: " + id));
    }

    public List<AnomalyRule> findAll() {
        return anomalyRuleDao.findAll();
    }

    public List<AnomalyRule> findEnabledRules() {
        return anomalyRuleDao.findEnabled();
    }

    public List<AnomalyRule> findBySeverity(String severity) {
        if (severity == null || severity.isBlank()) {
            throw new IllegalArgumentException("Anomaly severity is required.");
        }
        return anomalyRuleDao.findBySeverity(severity.trim().toUpperCase());
    }

    public List<AnomalyRule> findByMetricName(String metricName) {
        if (metricName == null || metricName.isBlank()) {
            throw new IllegalArgumentException("Metric name is required.");
        }
        return anomalyRuleDao.findByMetricName(metricName.trim());
    }

    public List<AnomalyRule> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        return anomalyRuleDao.search(keyword.trim());
    }

    public void update(AnomalyRule anomalyRule) {
        validate(anomalyRule);
        if (anomalyRule.getId() <= 0) {
            throw new IllegalArgumentException("Anomaly rule id is required for updates.");
        }
        anomalyRuleDao.update(anomalyRule);
    }

    public void deleteById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Anomaly rule id must be positive.");
        }
        anomalyRuleDao.deleteById(id);
    }

    private void validate(AnomalyRule anomalyRule) {
        if (anomalyRule == null) {
            throw new IllegalArgumentException("Anomaly rule cannot be null.");
        }
        if (anomalyRule.getName() == null || anomalyRule.getName().isBlank()) {
            throw new IllegalArgumentException("Anomaly rule name is required.");
        }
        if (anomalyRule.getSeverity() == null || anomalyRule.getSeverity().isBlank()) {
            throw new IllegalArgumentException("Anomaly severity is required.");
        }
        if (anomalyRule.getMetricName() == null || anomalyRule.getMetricName().isBlank()) {
            throw new IllegalArgumentException("Metric name is required for anomaly detection rules.");
        }
        if (Double.isNaN(anomalyRule.getThresholdValue()) || Double.isInfinite(anomalyRule.getThresholdValue())) {
            throw new IllegalArgumentException("Threshold value must be a finite numeric value.");
        }
    }
}
