package com.spacemission.dao.impl;

import com.spacemission.config.DatabaseConnection;
import com.spacemission.dao.interfaces.AnomalyRuleDao;
import com.spacemission.model.anomaly.AnomalyRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAnomalyRuleDao implements AnomalyRuleDao {

    @Override
    public AnomalyRule save(AnomalyRule anomalyRule) {
        String sql = "INSERT INTO anomaly_rules (name, severity, enabled, threshold_value, metric_name, description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, anomalyRule.getName());
            statement.setString(2, anomalyRule.getSeverity());
            statement.setBoolean(3, anomalyRule.isEnabled());
            if (Double.isNaN(anomalyRule.getThresholdValue()) || Double.isInfinite(anomalyRule.getThresholdValue())) {
                throw new IllegalArgumentException("Anomaly rule threshold value must be finite.");
            }
            statement.setDouble(4, anomalyRule.getThresholdValue());
            statement.setString(5, anomalyRule.getMetricName());
            statement.setString(6, anomalyRule.getDescription());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    anomalyRule.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save anomaly rule.", e);
        }

        return anomalyRule;
    }

    @Override
    public Optional<AnomalyRule> findById(int id) {
        String sql = "SELECT * FROM anomaly_rules WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find anomaly rule by id.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<AnomalyRule> findAll() {
        String sql = "SELECT * FROM anomaly_rules ORDER BY id ASC";
        return query(sql, new Object[0]);
    }

    @Override
    public List<AnomalyRule> findEnabled() {
        String sql = "SELECT * FROM anomaly_rules WHERE enabled = TRUE ORDER BY id ASC";
        return query(sql, new Object[0]);
    }

    @Override
    public List<AnomalyRule> findBySeverity(String severity) {
        String sql = "SELECT * FROM anomaly_rules WHERE severity = ? ORDER BY id ASC";
        return query(sql, new Object[]{severity});
    }

    @Override
    public List<AnomalyRule> findByMetricName(String metricName) {
        String sql = "SELECT * FROM anomaly_rules WHERE metric_name = ? ORDER BY id ASC";
        return query(sql, new Object[]{metricName});
    }

    @Override
    public List<AnomalyRule> search(String keyword) {
        String sql = "SELECT * FROM anomaly_rules WHERE LOWER(name) LIKE ? OR LOWER(description) LIKE ? OR LOWER(metric_name) LIKE ? ORDER BY id ASC";
        String searchTerm = "%" + keyword.trim().toLowerCase() + "%";
        return query(sql, new Object[]{searchTerm, searchTerm, searchTerm});
    }

    @Override
    public void update(AnomalyRule anomalyRule) {
        String sql = "UPDATE anomaly_rules SET name = ?, severity = ?, enabled = ?, threshold_value = ?, metric_name = ?, description = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, anomalyRule.getName());
            statement.setString(2, anomalyRule.getSeverity());
            statement.setBoolean(3, anomalyRule.isEnabled());
            statement.setDouble(4, anomalyRule.getThresholdValue());
            statement.setString(5, anomalyRule.getMetricName());
            statement.setString(6, anomalyRule.getDescription());
            statement.setInt(7, anomalyRule.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update anomaly rule.", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM anomaly_rules WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete anomaly rule.", e);
        }
    }

    private List<AnomalyRule> query(String sql, Object[] params) {
        List<AnomalyRule> rules = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rules.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to query anomaly rules.", e);
        }

        return rules;
    }

    private AnomalyRule mapRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String severity = resultSet.getString("severity");
        boolean enabled = resultSet.getBoolean("enabled");
        double thresholdValue = resultSet.getDouble("threshold_value");
        String metricName = resultSet.getString("metric_name");
        String description = resultSet.getString("description");

        return new AnomalyRule(id, name, severity, enabled, thresholdValue, metricName, description);
    }
}
