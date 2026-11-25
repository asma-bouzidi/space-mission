package com.spacemission.config;

import com.spacemission.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String URL = DatabaseConfig.getUrl();
    private static final String USER = DatabaseConfig.getUser();
    private static final String PASSWORD = DatabaseConfig.getPassword();

    private DatabaseConnection() {
    }

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("MySQL JDBC driver not found.", e);
        } catch (SQLException e) {
            throw new DatabaseException("Unable to connect to the database.", e);
        }
    }
}
