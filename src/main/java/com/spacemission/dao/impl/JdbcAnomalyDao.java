package com.spacemission.dao.impl;

import com.spacemission.config.DatabaseConnection;
import com.spacemission.dao.interfaces.AnomalyDao;
import com.spacemission.model.anomaly.Anomaly;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAnomalyDao implements AnomalyDao {

    @Override
    public Anomaly save(Anomaly anomaly) {
        String sql = "INSERT INTO anomalies (spacecraft_id, mission_id, rule_id, severity, title, description, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setNullableInt(statement, 1, anomaly.getSpacecraftId());
            setNullableInt(statement, 2, anomaly.getMissionId());
            setNullableInt(statement, 3, anomaly.getRuleId());
            statement.setString(4, anomaly.getSeverity());
            statement.setString(5, anomaly.getTitle());
            statement.setString(6, anomaly.getDescription());
            statement.setString(7, anomaly.getStatus());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    anomaly.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save anomaly.", e);
        }

        return anomaly;
    }

    @Override
    public Optional<Anomaly> findById(long id) {
        String sql = "SELECT * FROM anomalies WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find anomaly by id.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Anomaly> findAll() {
        String sql = "SELECT * FROM anomalies ORDER BY created_at DESC";
        List<Anomaly> anomalies = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                anomalies.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to list anomalies.", e);
        }

        return anomalies;
    }

    @Override
    public List<Anomaly> findOpen() {
        String sql = "SELECT * FROM anomalies WHERE status = 'OPEN' ORDER BY created_at DESC";
        List<Anomaly> anomalies = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                anomalies.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to list open anomalies.", e);
        }

        return anomalies;
    }

    @Override
    public List<Anomaly> findBySeverity(String severity) {
        String sql = "SELECT * FROM anomalies WHERE severity = ? ORDER BY created_at DESC";
        List<Anomaly> anomalies = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, severity);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    anomalies.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find anomalies by severity.", e);
        }

        return anomalies;
    }

    @Override
    public void update(Anomaly anomaly) {
        String sql = "UPDATE anomalies SET spacecraft_id = ?, mission_id = ?, rule_id = ?, severity = ?, title = ?, description = ?, status = ?, resolved_at = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setNullableInt(statement, 1, anomaly.getSpacecraftId());
            setNullableInt(statement, 2, anomaly.getMissionId());
            setNullableInt(statement, 3, anomaly.getRuleId());
            statement.setString(4, anomaly.getSeverity());
            statement.setString(5, anomaly.getTitle());
            statement.setString(6, anomaly.getDescription());
            statement.setString(7, anomaly.getStatus());
            if (anomaly.getResolvedAt() == null) {
                statement.setNull(8, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(8, Timestamp.valueOf(anomaly.getResolvedAt()));
            }
            statement.setLong(9, anomaly.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update anomaly.", e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM anomalies WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete anomaly.", e);
        }
    }

    private void setNullableInt(PreparedStatement statement, int parameterIndex, int value) throws SQLException {
        if (value <= 0) {
            statement.setNull(parameterIndex, Types.INTEGER);
        } else {
            statement.setInt(parameterIndex, value);
        }
    }

    private Anomaly mapRow(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp resolvedAt = resultSet.getTimestamp("resolved_at");

        return new Anomaly(
                resultSet.getInt("id"),
                resultSet.getInt("spacecraft_id"),
                resultSet.getInt("mission_id"),
                resultSet.getInt("rule_id"),
                resultSet.getString("severity"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getString("status"),
                createdAt == null ? null : createdAt.toLocalDateTime(),
                resolvedAt == null ? null : resolvedAt.toLocalDateTime()
        );
    }
}
