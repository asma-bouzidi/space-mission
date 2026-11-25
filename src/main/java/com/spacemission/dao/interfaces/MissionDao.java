package com.spacemission.dao.interfaces;

import com.spacemission.model.mission.Mission;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface MissionDao {
    void save(Mission mission);
    Optional<Mission> findById(int id);
    default Optional<Mission> findById(int id, Connection connection) {
        return findById(id);
    }
    List<Mission> findAll();
    void update(Mission mission);
    default void update(Mission mission, Connection connection) {
        update(mission);
    }
    void deleteById(int id);
    List<Mission> findByStatus(String status);
}
