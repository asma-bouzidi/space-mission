package com.spacemission.dao.impl;

import com.spacemission.config.DatabaseConnection;
import com.spacemission.dao.interfaces.MissionDao;
import com.spacemission.enums.MissionPriority;
import com.spacemission.enums.MissionStatus;
import com.spacemission.model.mission.Mission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMissionDao implements MissionDao {

    @Override
    public void save(Mission mission) {
        String sql = "INSERT INTO missions (mission_code, name, mission_type, description, priority, status, start_date, expected_end_date, actual_end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, mission.getMissionCode());
            statement.setString(2, mission.getName());
            statement.setString(3, mission.getMissionType());
            statement.setString(4, mission.getDescription());
            statement.setString(5, mission.getPriority().name());
            statement.setString(6, mission.getStatus().name());
            statement.setTimestamp(7, mission.getStartDate() == null ? null : Timestamp.valueOf(mission.getStartDate()));
            statement.setTimestamp(8, mission.getExpectedEndDate() == null ? null : Timestamp.valueOf(mission.getExpectedEndDate()));
            statement.setTimestamp(9, mission.getActualEndDate() == null ? null : Timestamp.valueOf(mission.getActualEndDate()));
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mission.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save mission.", e);
        }
    }

    @Override
    public Optional<Mission> findById(int id) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find mission by id.", e);
        }
    }

    @Override
    public Optional<Mission> findById(int id, Connection connection) {
        String sql = "SELECT * FROM missions WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Mission mission = mapRowToMission(resultSet);
                    loadAssignedSpacecraft(connection, mission);
                    return Optional.of(mission);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find mission by id.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Mission> findAll() {
        String sql = "SELECT * FROM missions ORDER BY id";
        List<Mission> missions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Mission mission = mapRowToMission(resultSet);
                loadAssignedSpacecraft(connection, mission);
                missions.add(mission);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve mission list.", e);
        }

        return missions;
    }

    @Override
    public void update(Mission mission) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            update(mission, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update mission.", e);
        }
    }

    @Override
    public void update(Mission mission, Connection connection) {
        String sql = "UPDATE missions SET mission_code = ?, name = ?, mission_type = ?, description = ?, priority = ?, status = ?, start_date = ?, expected_end_date = ?, actual_end_date = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mission.getMissionCode());
            statement.setString(2, mission.getName());
            statement.setString(3, mission.getMissionType());
            statement.setString(4, mission.getDescription());
            statement.setString(5, mission.getPriority().name());
            statement.setString(6, mission.getStatus().name());
            statement.setTimestamp(7, mission.getStartDate() == null ? null : Timestamp.valueOf(mission.getStartDate()));
            statement.setTimestamp(8, mission.getExpectedEndDate() == null ? null : Timestamp.valueOf(mission.getExpectedEndDate()));
            statement.setTimestamp(9, mission.getActualEndDate() == null ? null : Timestamp.valueOf(mission.getActualEndDate()));
            statement.setInt(10, mission.getId());
            statement.executeUpdate();
            persistAssignedSpacecraft(connection, mission.getId(), mission.getAssignedSpacecraftIds());
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update mission.", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM missions WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete mission.", e);
        }
    }

    @Override
    public List<Mission> findByStatus(String status) {
        String sql = "SELECT * FROM missions WHERE status = ? ORDER BY id";
        List<Mission> missions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    missions.add(mapRowToMission(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve missions by status.", e);
        }

        return missions;
    }

    private void loadAssignedSpacecraft(Connection connection, Mission mission) throws SQLException {
        String sql = "SELECT spacecraft_id FROM mission_spacecraft WHERE mission_id = ? ORDER BY assigned_at";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, mission.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    mission.assignSpacecraft(resultSet.getInt("spacecraft_id"));
                }
            }
        }
    }

    private void persistAssignedSpacecraft(Connection connection, int missionId, List<Integer> spacecraftIds) throws SQLException {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM mission_spacecraft WHERE mission_id = ?")) {
            deleteStatement.setInt(1, missionId);
            deleteStatement.executeUpdate();
        }

        if (spacecraftIds == null || spacecraftIds.isEmpty()) {
            return;
        }

        try (PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO mission_spacecraft (mission_id, spacecraft_id) VALUES (?, ?)")) {
            for (Integer spacecraftId : spacecraftIds) {
                if (spacecraftId == null || spacecraftId <= 0) {
                    continue;
                }
                insertStatement.setInt(1, missionId);
                insertStatement.setInt(2, spacecraftId);
                insertStatement.addBatch();
            }
            if (spacecraftIds.stream().anyMatch(id -> id != null && id > 0)) {
                insertStatement.executeBatch();
            }
        }
    }

    private Mission mapRowToMission(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String code = resultSet.getString("mission_code");
        String description = resultSet.getString("description");
        MissionPriority priority = MissionPriority.valueOf(resultSet.getString("priority"));
        MissionStatus status = MissionStatus.valueOf(resultSet.getString("status"));

        Mission mission = new Mission(id, name, code, description, priority, status);

        Timestamp startTimestamp = resultSet.getTimestamp("start_date");
        Timestamp expectedTimestamp = resultSet.getTimestamp("expected_end_date");
        Timestamp actualTimestamp = resultSet.getTimestamp("actual_end_date");

        if (startTimestamp != null) {
            mission.setStartDate(startTimestamp.toLocalDateTime());
        }
        if (expectedTimestamp != null) {
            mission.setExpectedEndDate(expectedTimestamp.toLocalDateTime());
        }
        if (actualTimestamp != null) {
            mission.setActualEndDate(actualTimestamp.toLocalDateTime());
        }

        mission.setMissionType(resultSet.getString("mission_type"));
        return mission;
    }
}
