package com.spacemission.service.resource;

import com.spacemission.config.DatabaseTransactionManager;
import com.spacemission.dao.impl.JdbcResourceDao;
import com.spacemission.dao.interfaces.ResourceDao;
import com.spacemission.exception.InsufficientResourcesException;
import com.spacemission.model.resource.Resource;

import java.util.List;
import java.util.Objects;

public class ResourceService {
    private final ResourceDao resourceDao;

    public ResourceService() {
        this(new JdbcResourceDao());
    }

    public ResourceService(ResourceDao resourceDao) {
        this.resourceDao = Objects.requireNonNull(resourceDao, "Resource DAO cannot be null.");
    }

    public Resource createResource(Resource resource) {
        validate(resource);
        if (resourceDao.findByName(resource.getName()).isPresent()) {
            throw new IllegalArgumentException("Resource already exists: " + resource.getName());
        }
        return resourceDao.save(resource);
    }

    public void updateResource(Resource resource) {
        validate(resource);
        if (resource.getId() <= 0) {
            throw new IllegalArgumentException("Resource id is required for updates.");
        }
        resourceDao.update(resource);
    }

    public void deleteResource(int resourceId) {
        if (resourceId <= 0) {
            throw new IllegalArgumentException("Resource id must be positive.");
        }
        resourceDao.deleteById(resourceId);
    }

    public Resource findById(int resourceId) {
        return resourceDao.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + resourceId));
    }

    public Resource findByName(String resourceName) {
        if (resourceName == null || resourceName.isBlank()) {
            throw new IllegalArgumentException("Resource name is required.");
        }
        return resourceDao.findByName(resourceName)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + resourceName));
    }

    public List<Resource> findAll() {
        return resourceDao.findAll();
    }

    public void addResource(Resource resource) {
        createResource(resource);
    }

    public boolean allocateResource(String resourceName, double quantity) {
        Resource resource = findByName(resourceName);
        if (!resource.allocate(quantity)) {
            throw new InsufficientResourcesException("Insufficient resource availability for " + resourceName);
        }
        resourceDao.update(resource);
        return true;
    }

    public boolean allocateResourceTransactional(String resourceName, double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Allocation quantity must be positive.");
        }

        return DatabaseTransactionManager.executeInTransaction(connection -> {
            Resource resource = resourceDao.findByName(resourceName, connection)
                    .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + resourceName));
            if (!resource.allocate(quantity)) {
                throw new InsufficientResourcesException("Insufficient resource availability for " + resourceName);
            }
            resourceDao.update(resource, connection);
            return true;
        });
    }

    public void replenishResource(String resourceName, double quantity) {
        Resource resource = findByName(resourceName);
        resource.replenish(quantity);
        resourceDao.update(resource);
    }

    public double getAvailableQuantity(String resourceName) {
        return findByName(resourceName).getAvailableQuantity();
    }

    private void validate(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null.");
        }
        if (resource.getName() == null || resource.getName().isBlank()) {
            throw new IllegalArgumentException("Resource name cannot be empty.");
        }
        if (resource.getType() == null) {
            throw new IllegalArgumentException("Resource type cannot be null.");
        }
        if (resource.getTotalQuantity() < 0) {
            throw new IllegalArgumentException("Total quantity cannot be negative.");
        }
    }
}
