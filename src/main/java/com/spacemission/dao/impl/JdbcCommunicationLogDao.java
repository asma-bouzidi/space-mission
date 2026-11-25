package com.spacemission.dao.impl;

import com.spacemission.config.DatabaseConnection;
import com.spacemission.dao.interfaces.CommunicationLogDao;
import com.spacemission.model.communication.CommunicationLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCommunicationLogDao implements CommunicationLogDao {

    @Override
    public CommunicationLog save(CommunicationLog log) {
        String sql = "INSERT INTO communication_logs (spacecraft_id, mission_id, sender, recipient, command_type, payload, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setNullableInt(statement, 1, log.getSpacecraftId());
            setNullableInt(statement, 2, log.getMissionId());
            statement.setString(3, log.getSender());
            statement.setString(4, log.getRecipient());
            statement.setString(5, log.getCommandType());
            statement.setString(6, log.getPayload());
            statement.setString(7, log.getStatus());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    log.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save communication log.", e);
        }

        return log;
    }

    @Override
    public Optional<CommunicationLog> findById(long id) {
        String sql = "SELECT * FROM communication_logs WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find communication log by id.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<CommunicationLog> findAll() {
        String sql = "SELECT * FROM communication_logs ORDER BY sent_at DESC";
        List<CommunicationLog> logs = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                logs.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve communication logs.", e);
        }

        return logs;
    }

    @Override
    public List<CommunicationLog> findBySpacecraftId(int spacecraftId) {
        String sql = "SELECT * FROM communication_logs WHERE spacecraft_id = ? ORDER BY sent_at DESC";
        List<CommunicationLog> logs = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, spacecraftId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve communication logs by spacecraft.", e);
        }

        return logs;
    }

    @Override
    public void update(CommunicationLog log) {
        String sql = "UPDATE communication_logs SET spacecraft_id = ?, mission_id = ?, sender = ?, recipient = ?, command_type = ?, payload = ?, status = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setNullableInt(statement, 1, log.getSpacecraftId());
            setNullableInt(statement, 2, log.getMissionId());
            statement.setString(3, log.getSender());
            statement.setString(4, log.getRecipient());
            statement.setString(5, log.getCommandType());
            statement.setString(6, log.getPayload());
            statement.setString(7, log.getStatus());
            statement.setLong(8, log.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update communication log.", e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM communication_logs WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete communication log.", e);
        }
    }

    private void setNullableInt(PreparedStatement statement, int parameterIndex, int value) throws SQLException {
        if (value <= 0) {
            statement.setNull(parameterIndex, Types.INTEGER);
        } else {
            statement.setInt(parameterIndex, value);
        }
    }

    private CommunicationLog mapRow(ResultSet resultSet) throws SQLException {
        Timestamp sentAt = resultSet.getTimestamp("sent_at");
        return new CommunicationLog(
                resultSet.getInt("id"),
                resultSet.getInt("spacecraft_id"),
                resultSet.getInt("mission_id"),
                resultSet.getString("sender"),
                resultSet.getString("recipient"),
                resultSet.getString("command_type"),
                resultSet.getString("payload"),
                resultSet.getString("status"),
                sentAt == null ? null : sentAt.toLocalDateTime()
        );
    }
}
