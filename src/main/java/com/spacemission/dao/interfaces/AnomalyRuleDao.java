package com.spacemission.dao.interfaces;

import com.spacemission.model.anomaly.AnomalyRule;

import java.util.List;
import java.util.Optional;

public interface AnomalyRuleDao {
    AnomalyRule save(AnomalyRule anomalyRule);

    Optional<AnomalyRule> findById(int id);

    List<AnomalyRule> findAll();

    List<AnomalyRule> findEnabled();

    List<AnomalyRule> findBySeverity(String severity);

    List<AnomalyRule> findByMetricName(String metricName);

    List<AnomalyRule> search(String keyword);

    void update(AnomalyRule anomalyRule);

    void deleteById(int id);
}
