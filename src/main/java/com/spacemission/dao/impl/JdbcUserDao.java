package com.spacemission.dao.impl;

import com.spacemission.config.DatabaseConnection;
import com.spacemission.dao.interfaces.UserDao;
import com.spacemission.model.user.Role;
import com.spacemission.model.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcUserDao implements UserDao {

    @Override
    public void save(User user, String passwordHash) {
        String sql = "INSERT INTO users (username, password_hash, email, full_name, enabled) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, passwordHash);
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getFullName());
            statement.setBoolean(5, user.isEnabled());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }

            persistRoles(connection, user.getId(), user.getRoles());
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save user.", e);
        }
    }

    @Override
    public Optional<User> findById(int id) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find user by id.", e);
        }
    }

    @Override
    public Optional<User> findById(int id, Connection connection) {
        String sql = "SELECT u.*, GROUP_CONCAT(DISTINCT r.name ORDER BY r.name SEPARATOR ',') AS role_names " +
                "FROM users u LEFT JOIN user_roles ur ON ur.user_id = u.id " +
                "LEFT JOIN roles r ON r.id = ur.role_id WHERE u.id = ? GROUP BY u.id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find user by id.", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return findByUsername(username, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find user by username.", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username, Connection connection) {
        String sql = "SELECT u.*, GROUP_CONCAT(DISTINCT r.name ORDER BY r.name SEPARATOR ',') AS role_names " +
                "FROM users u LEFT JOIN user_roles ur ON ur.user_id = u.id " +
                "LEFT JOIN roles r ON r.id = ur.role_id WHERE u.username = ? GROUP BY u.id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find user by username.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT u.*, GROUP_CONCAT(DISTINCT r.name ORDER BY r.name SEPARATOR ',') AS role_names " +
                "FROM users u LEFT JOIN user_roles ur ON ur.user_id = u.id " +
                "LEFT JOIN roles r ON r.id = ur.role_id GROUP BY u.id ORDER BY u.id";

        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Integer, User> userMap = new LinkedHashMap<>();
            while (resultSet.next()) {
                User user = mapUser(resultSet);
                userMap.putIfAbsent(user.getId(), user);
            }
            users.addAll(userMap.values());
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve users.", e);
        }

        return users;
    }

    @Override
    public void update(User user) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            update(user, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update user.", e);
        }
    }

    @Override
    public void update(User user, Connection connection) {
        String sql = "UPDATE users SET username = ?, email = ?, full_name = ?, enabled = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getFullName());
            statement.setBoolean(4, user.isEnabled());
            statement.setInt(5, user.getId());
            statement.executeUpdate();

            persistRoles(connection, user.getId(), user.getRoles());
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update user.", e);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement deleteRoles = connection.prepareStatement("DELETE FROM user_roles WHERE user_id = ?")) {
                deleteRoles.setInt(1, id);
                deleteRoles.executeUpdate();
            }

            try (PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
                deleteUser.setInt(1, id);
                deleteUser.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete user.", e);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setEmail(resultSet.getString("email"));
        user.setFullName(resultSet.getString("full_name"));
        user.setEnabled(resultSet.getBoolean("enabled"));

        String roleNames = resultSet.getString("role_names");
        if (roleNames != null && !roleNames.isBlank()) {
            String[] roles = roleNames.split(",");
            for (String roleName : roles) {
                if (!roleName.isBlank()) {
                    user.addRole(Role.valueOf(roleName.trim()));
                }
            }
        }

        return user;
    }

    private void persistRoles(Connection connection, int userId, java.util.Set<Role> roles) throws SQLException {
        try (PreparedStatement deleteRoles = connection.prepareStatement("DELETE FROM user_roles WHERE user_id = ?")) {
            deleteRoles.setInt(1, userId);
            deleteRoles.executeUpdate();
        }

        for (Role role : roles) {
            int roleId = fetchRoleId(connection, role.name());
            if (roleId <= 0) {
                continue;
            }

            try (PreparedStatement insertRole = connection.prepareStatement("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)")) {
                insertRole.setInt(1, userId);
                insertRole.setInt(2, roleId);
                insertRole.executeUpdate();
            }
        }
    }

    private int fetchRoleId(Connection connection, String roleName) throws SQLException {
        String sql = "SELECT id FROM roles WHERE name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }

        return -1;
    }
}
