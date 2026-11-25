package com.spacemission.service.user;

import com.spacemission.config.DatabaseTransactionManager;
import com.spacemission.dao.impl.JdbcUserDao;
import com.spacemission.dao.interfaces.UserDao;
import com.spacemission.model.user.Role;
import com.spacemission.model.user.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Objects;

public class UserService {
    private final UserDao userDao;

    public UserService() {
        this(new JdbcUserDao());
    }

    public UserService(UserDao userDao) {
        this.userDao = Objects.requireNonNull(userDao, "User DAO cannot be null.");
    }

    public User createUser(User user, String rawPassword) {
        validateUser(user);
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (userDao.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (user.getRoles().isEmpty()) {
            user.addRole(Role.MISSION_CONTROL_OPERATOR);
        }
        user.setPasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
        userDao.save(user, user.getPasswordHash());
        return user;
    }

    public void updateUser(User user) {
        validateUser(user);
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("User id is required for updates.");
        }
        if (user.getRoles().isEmpty()) {
            user.addRole(Role.MISSION_CONTROL_OPERATOR);
        }
        userDao.update(user);
    }

    public void updateUserTransactional(User user) {
        validateUser(user);
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("User id is required for updates.");
        }
        if (user.getRoles().isEmpty()) {
            user.addRole(Role.MISSION_CONTROL_OPERATOR);
        }

        DatabaseTransactionManager.executeInTransaction(connection -> {
            if (userDao.findById(user.getId(), connection).isEmpty()) {
                throw new IllegalArgumentException("User not found: " + user.getId());
            }
            userDao.update(user, connection);
            return null;
        });
    }

    public void deleteUser(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User id must be positive.");
        }
        userDao.deleteById(userId);
    }

    public User findById(int userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    public User findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        return userDao.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (user.getFullName() == null || user.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
    }
}
