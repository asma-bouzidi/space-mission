package com.spacemission.dao.impl;

import com.spacemission.config.DatabaseConnection;
import com.spacemission.dao.interfaces.ResourceDao;
import com.spacemission.enums.ResourceType;
import com.spacemission.model.resource.Resource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcResourceDao implements ResourceDao {

    @Override
    public Resource save(Resource resource) {
        String sql = "INSERT INTO resources (name, type, total_quantity, available_quantity, unit) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, resource.getName());
            statement.setString(2, resource.getType().name());
            statement.setDouble(3, resource.getTotalQuantity());
            statement.setDouble(4, resource.getAvailableQuantity());
            statement.setString(5, resource.getUnit());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    resource.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save resource.", e);
        }

        return resource;
    }

    @Override
    public Optional<Resource> findById(int id) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find resource by id.", e);
        }
    }

    @Override
    public Optional<Resource> findById(int id, Connection connection) {
        String sql = "SELECT * FROM resources WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find resource by id.", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Resource> findByName(String name) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return findByName(name, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find resource by name.", e);
        }
    }

    @Override
    public Optional<Resource> findByName(String name, Connection connection) {
        String sql = "SELECT * FROM resources WHERE name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find resource by name.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Resource> findAll() {
        String sql = "SELECT * FROM resources ORDER BY id";
        List<Resource> resources = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                resources.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve resource list.", e);
        }

        return resources;
    }

    @Override
    public void update(Resource resource) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            update(resource, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update resource.", e);
        }
    }

    @Override
    public void update(Resource resource, Connection connection) {
        String sql = "UPDATE resources SET name = ?, type = ?, total_quantity = ?, available_quantity = ?, unit = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resource.getName());
            statement.setString(2, resource.getType().name());
            statement.setDouble(3, resource.getTotalQuantity());
            statement.setDouble(4, resource.getAvailableQuantity());
            statement.setString(5, resource.getUnit());
            statement.setInt(6, resource.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update resource.", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM resources WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete resource.", e);
        }
    }

    private Resource mapRow(ResultSet resultSet) throws SQLException {
        Resource resource = new Resource(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                ResourceType.valueOf(resultSet.getString("type")),
                resultSet.getDouble("total_quantity"),
                resultSet.getString("unit")
        );
        resource.setAvailableQuantity(resultSet.getDouble("available_quantity"));
        return resource;
    }
}
