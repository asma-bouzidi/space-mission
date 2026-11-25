package com.spacemission.dao.impl;

import com.spacemission.config.DatabaseConnection;
import com.spacemission.dao.interfaces.TelemetryDao;
import com.spacemission.model.telemetry.Telemetry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTelemetryDao implements TelemetryDao {

    @Override
    public Telemetry save(Telemetry telemetry) {
        String sql = "INSERT INTO telemetry (spacecraft_id, temperature, voltage, battery_percentage, fuel_percentage, altitude, velocity, pressure, signal_strength, solar_panel_efficiency, communication_quality) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, telemetry.getSpacecraftId());
            statement.setDouble(2, telemetry.getTemperature());
            statement.setDouble(3, telemetry.getVoltage());
            statement.setDouble(4, telemetry.getBatteryPercentage());
            statement.setDouble(5, telemetry.getFuelPercentage());
            statement.setDouble(6, telemetry.getAltitude());
            statement.setDouble(7, telemetry.getVelocity());
            statement.setDouble(8, telemetry.getPressure());
            statement.setDouble(9, telemetry.getSignalStrength());
            statement.setDouble(10, telemetry.getSolarPanelEfficiency());
            statement.setDouble(11, telemetry.getCommunicationQuality());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    telemetry.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save telemetry snapshot.", e);
        }

        return telemetry;
    }

    @Override
    public Optional<Telemetry> findById(long id) {
        String sql = "SELECT * FROM telemetry WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find telemetry by id.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Telemetry> findAll() {
        String sql = "SELECT * FROM telemetry ORDER BY captured_at DESC";
        List<Telemetry> telemetryList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                telemetryList.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve telemetry list.", e);
        }

        return telemetryList;
    }

    @Override
    public List<Telemetry> findBySpacecraftId(int spacecraftId) {
        String sql = "SELECT * FROM telemetry WHERE spacecraft_id = ? ORDER BY captured_at DESC";
        List<Telemetry> telemetryList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, spacecraftId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    telemetryList.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve telemetry by spacecraft.", e);
        }

        return telemetryList;
    }

    @Override
    public void update(Telemetry telemetry) {
        String sql = "UPDATE telemetry SET spacecraft_id = ?, temperature = ?, voltage = ?, battery_percentage = ?, fuel_percentage = ?, altitude = ?, velocity = ?, pressure = ?, signal_strength = ?, solar_panel_efficiency = ?, communication_quality = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, telemetry.getSpacecraftId());
            statement.setDouble(2, telemetry.getTemperature());
            statement.setDouble(3, telemetry.getVoltage());
            statement.setDouble(4, telemetry.getBatteryPercentage());
            statement.setDouble(5, telemetry.getFuelPercentage());
            statement.setDouble(6, telemetry.getAltitude());
            statement.setDouble(7, telemetry.getVelocity());
            statement.setDouble(8, telemetry.getPressure());
            statement.setDouble(9, telemetry.getSignalStrength());
            statement.setDouble(10, telemetry.getSolarPanelEfficiency());
            statement.setDouble(11, telemetry.getCommunicationQuality());
            statement.setLong(12, telemetry.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update telemetry.", e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM telemetry WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete telemetry.", e);
        }
    }

    private Telemetry mapRow(ResultSet resultSet) throws SQLException {
        Timestamp capturedAt = resultSet.getTimestamp("captured_at");
        return new Telemetry(
                resultSet.getInt("id"),
                resultSet.getInt("spacecraft_id"),
                resultSet.getDouble("temperature"),
                resultSet.getDouble("voltage"),
                resultSet.getDouble("battery_percentage"),
                resultSet.getDouble("fuel_percentage"),
                resultSet.getDouble("altitude"),
                resultSet.getDouble("velocity"),
                resultSet.getDouble("pressure"),
                resultSet.getDouble("signal_strength"),
                resultSet.getDouble("solar_panel_efficiency"),
                resultSet.getDouble("communication_quality"),
                capturedAt == null ? null : capturedAt.toLocalDateTime()
        );
    }
}
