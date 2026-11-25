package com.spacemission.config;

import com.spacemission.exception.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseTransactionManager {

    @FunctionalInterface
    public interface TransactionCallback<T> {
        T doInTransaction(Connection connection) throws SQLException;
    }

    private DatabaseTransactionManager() {
    }

    public static <T> T executeInTransaction(TransactionCallback<T> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Transaction callback cannot be null.");
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                T result = callback.doInTransaction(connection);
                connection.commit();
                return result;
            } catch (SQLException | RuntimeException | Error ex) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
                if (ex instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                if (ex instanceof Error error) {
                    throw error;
                }
                throw new DatabaseException("Transaction failed.", ex);
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Unable to manage transaction.", e);
        }
    }
}
