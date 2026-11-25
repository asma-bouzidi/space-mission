package com.spacemission.dao.impl;

import com.spacemission.config.DatabaseConnection;
import com.spacemission.dao.interfaces.SpacecraftDao;
import com.spacemission.enums.SpacecraftStatus;
import com.spacemission.enums.SpacecraftType;
import com.spacemission.model.spacecraft.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcSpacecraftDao implements SpacecraftDao {

    @Override
    public void save(Spacecraft spacecraft) {
        String sql = "INSERT INTO spacecraft (name, serial_number, spacecraft_type_id, manufacturer, launch_date, status, fuel_level, battery_level, altitude, velocity, orbit_type, temperature, communication_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, spacecraft.getName());
            statement.setString(2, spacecraft.getSerialNumber());
            statement.setInt(3, mapTypeToDatabaseId(spacecraft.getSpacecraftType()));
            statement.setString(4, spacecraft.getManufacturer());
            statement.setDate(5, Date.valueOf(spacecraft.getLaunchDate()));
            statement.setString(6, spacecraft.getStatus().name());
            statement.setDouble(7, spacecraft.getFuelLevel());
            statement.setDouble(8, spacecraft.getBatteryLevel());
            statement.setDouble(9, spacecraft.getAltitude());
            statement.setDouble(10, spacecraft.getVelocity());
            statement.setString(11, spacecraft.getOrbitType());
            statement.setDouble(12, spacecraft.getTemperature());
            statement.setString(13, spacecraft.getCommunicationStatus());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    spacecraft.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save spacecraft.", e);
        }
    }

    @Override
    public Optional<Spacecraft> findById(int id) {
        String sql = "SELECT * FROM spacecraft WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToSpacecraft(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find spacecraft by id.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Spacecraft> findAll() {
        String sql = "SELECT * FROM spacecraft ORDER BY id";
        List<Spacecraft> spacecraft = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                spacecraft.add(mapRowToSpacecraft(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve spacecraft list.", e);
        }

        return spacecraft;
    }

    @Override
    public void update(Spacecraft spacecraft) {
        String sql = "UPDATE spacecraft SET name = ?, serial_number = ?, spacecraft_type_id = ?, manufacturer = ?, launch_date = ?, status = ?, fuel_level = ?, battery_level = ?, altitude = ?, velocity = ?, orbit_type = ?, temperature = ?, communication_status = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, spacecraft.getName());
            statement.setString(2, spacecraft.getSerialNumber());
            statement.setInt(3, mapTypeToDatabaseId(spacecraft.getSpacecraftType()));
            statement.setString(4, spacecraft.getManufacturer());
            statement.setDate(5, Date.valueOf(spacecraft.getLaunchDate()));
            statement.setString(6, spacecraft.getStatus().name());
            statement.setDouble(7, spacecraft.getFuelLevel());
            statement.setDouble(8, spacecraft.getBatteryLevel());
            statement.setDouble(9, spacecraft.getAltitude());
            statement.setDouble(10, spacecraft.getVelocity());
            statement.setString(11, spacecraft.getOrbitType());
            statement.setDouble(12, spacecraft.getTemperature());
            statement.setString(13, spacecraft.getCommunicationStatus());
            statement.setInt(14, spacecraft.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update spacecraft.", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM spacecraft WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete spacecraft.", e);
        }
    }

    @Override
    public List<Spacecraft> findByStatus(String status) {
        String sql = "SELECT * FROM spacecraft WHERE status = ?";
        List<Spacecraft> spacecraft = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    spacecraft.add(mapRowToSpacecraft(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve spacecraft by status.", e);
        }

        return spacecraft;
    }

    private Spacecraft mapRowToSpacecraft(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String serialNumber = resultSet.getString("serial_number");
        String manufacturer = resultSet.getString("manufacturer");
        SpacecraftType type = mapDatabaseIdToType(resultSet.getInt("spacecraft_type_id"));

        Spacecraft spacecraft = switch (type) {
            case COMMUNICATION_SATELLITE -> new CommunicationSatellite(id, name, serialNumber, manufacturer);
            case OBSERVATION_SATELLITE -> new ObservationSatellite(id, name, serialNumber, manufacturer);
            case NAVIGATION_SATELLITE -> new NavigationSatellite(id, name, serialNumber, manufacturer);
            case SCIENTIFIC_SATELLITE -> new ScientificSatellite(id, name, serialNumber, manufacturer);
            case EXPLORATION_SPACECRAFT -> new ExplorationSpacecraft(id, name, serialNumber, manufacturer);
        };

        spacecraft.setLaunchDate(resultSet.getDate("launch_date").toLocalDate());
        spacecraft.setStatus(SpacecraftStatus.valueOf(resultSet.getString("status")));
        spacecraft.setFuelLevel(resultSet.getDouble("fuel_level"));
        spacecraft.setBatteryLevel(resultSet.getDouble("battery_level"));
        spacecraft.setAltitude(resultSet.getDouble("altitude"));
        spacecraft.setVelocity(resultSet.getDouble("velocity"));
        spacecraft.setOrbitType(resultSet.getString("orbit_type"));
        spacecraft.setTemperature(resultSet.getDouble("temperature"));
        spacecraft.setCommunicationStatus(resultSet.getString("communication_status"));

        return spacecraft;
    }

    private int mapTypeToDatabaseId(SpacecraftType type) {
        return switch (type) {
            case COMMUNICATION_SATELLITE -> 1;
            case OBSERVATION_SATELLITE -> 2;
            case NAVIGATION_SATELLITE -> 3;
            case SCIENTIFIC_SATELLITE -> 4;
            case EXPLORATION_SPACECRAFT -> 5;
        };
    }

    private SpacecraftType mapDatabaseIdToType(int id) {
        return switch (id) {
            case 1 -> SpacecraftType.COMMUNICATION_SATELLITE;
            case 2 -> SpacecraftType.OBSERVATION_SATELLITE;
            case 3 -> SpacecraftType.NAVIGATION_SATELLITE;
            case 4 -> SpacecraftType.SCIENTIFIC_SATELLITE;
            case 5 -> SpacecraftType.EXPLORATION_SPACECRAFT;
            default -> throw new IllegalArgumentException("Unknown spacecraft type id: " + id);
        };
    }
}
