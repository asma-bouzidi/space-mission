package com.spacemission.dao.interfaces;

import com.spacemission.model.resource.Resource;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface ResourceDao {
    Resource save(Resource resource);
    Optional<Resource> findById(int id);
    default Optional<Resource> findById(int id, Connection connection) {
        return findById(id);
    }
    Optional<Resource> findByName(String name);
    List<Resource> findAll();
    void update(Resource resource);
    default void update(Resource resource, Connection connection) {
        update(resource);
    }
    default Optional<Resource> findByName(String name, Connection connection) {
        return findByName(name);
    }
    void deleteById(int id);
}
