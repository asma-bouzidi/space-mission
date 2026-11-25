package com.spacemission.dao.impl;

import com.spacemission.config.DatabaseConnection;
import com.spacemission.dao.interfaces.MissionTeamDao;
import com.spacemission.model.mission.MissionTeam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMissionTeamDao implements MissionTeamDao {

    @Override
    public MissionTeam save(MissionTeam missionTeam) {
        String sql = "INSERT INTO mission_team (mission_id, user_id, role_name) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, missionTeam.getMissionId());
            statement.setInt(2, missionTeam.getUserId());
            statement.setString(3, missionTeam.getRoleName());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    missionTeam.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save mission team member.", e);
        }

        return missionTeam;
    }

    @Override
    public Optional<MissionTeam> findById(int id) {
        String sql = "SELECT * FROM mission_team WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find mission team member by id.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<MissionTeam> findAll() {
        String sql = "SELECT * FROM mission_team ORDER BY assigned_at DESC, id DESC";
        List<MissionTeam> missionTeams = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                missionTeams.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to list mission team members.", e);
        }

        return missionTeams;
    }

    @Override
    public List<MissionTeam> findByMissionId(int missionId) {
        String sql = "SELECT * FROM mission_team WHERE mission_id = ? ORDER BY assigned_at DESC";
        List<MissionTeam> missionTeams = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, missionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    missionTeams.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find mission team by mission id.", e);
        }

        return missionTeams;
    }

    @Override
    public List<MissionTeam> findByUserId(int userId) {
        String sql = "SELECT * FROM mission_team WHERE user_id = ? ORDER BY assigned_at DESC";
        List<MissionTeam> missionTeams = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    missionTeams.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find mission team by user id.", e);
        }

        return missionTeams;
    }

    @Override
    public void update(MissionTeam missionTeam) {
        String sql = "UPDATE mission_team SET mission_id = ?, user_id = ?, role_name = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, missionTeam.getMissionId());
            statement.setInt(2, missionTeam.getUserId());
            statement.setString(3, missionTeam.getRoleName());
            statement.setInt(4, missionTeam.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update mission team member.", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM mission_team WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete mission team member.", e);
        }
    }

    private MissionTeam mapRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int missionId = resultSet.getInt("mission_id");
        int userId = resultSet.getInt("user_id");
        String roleName = resultSet.getString("role_name");
        Timestamp assignedAt = resultSet.getTimestamp("assigned_at");

        MissionTeam missionTeam = new MissionTeam(id, missionId, userId, roleName, assignedAt == null ? null : assignedAt.toLocalDateTime());
        if (assignedAt != null) {
            missionTeam.setAssignedAt(assignedAt.toLocalDateTime());
        }
        return missionTeam;
    }
}
