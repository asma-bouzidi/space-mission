package com.spacemission.dao.interfaces;

import com.spacemission.model.user.User;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    void save(User user, String passwordHash);
    Optional<User> findById(int id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    void update(User user);
    default void update(User user, Connection connection) {
        update(user);
    }
    default Optional<User> findByUsername(String username, Connection connection) {
        return findByUsername(username);
    }
    default Optional<User> findById(int id, Connection connection) {
        return findById(id);
    }
    void deleteById(int id);
}
