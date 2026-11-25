package com.spacemission.dao.interfaces;

import com.spacemission.model.spacecraft.Spacecraft;

import java.util.List;
import java.util.Optional;

public interface SpacecraftDao {
    void save(Spacecraft spacecraft);
    Optional<Spacecraft> findById(int id);
    List<Spacecraft> findAll();
    void update(Spacecraft spacecraft);
    void deleteById(int id);
    List<Spacecraft> findByStatus(String status);
}
