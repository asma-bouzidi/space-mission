package com.spacemission.dao.interfaces;

import com.spacemission.model.anomaly.Anomaly;

import java.util.List;
import java.util.Optional;

public interface AnomalyDao {
    Anomaly save(Anomaly anomaly);

    Optional<Anomaly> findById(long id);

    List<Anomaly> findAll();

    List<Anomaly> findOpen();

    List<Anomaly> findBySeverity(String severity);

    void update(Anomaly anomaly);

    void deleteById(long id);
}
