package com.spacemission.model.resource;

import com.spacemission.enums.ResourceType;

public class Resource {
    private int id;
    private String name;
    private final ResourceType type;
    private double totalQuantity;
    private double availableQuantity;
    private final String unit;

    public Resource(int id, String name, ResourceType type, double totalQuantity, String unit) {
        if (id < 0) {
            throw new IllegalArgumentException("Resource id cannot be negative.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Resource name cannot be empty.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Resource type cannot be null.");
        }
        if (totalQuantity < 0) {
            throw new IllegalArgumentException("Total quantity cannot be negative.");
        }

        this.id = id;
        this.name = name;
        this.type = type;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
        this.unit = unit == null ? "units" : unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Resource id cannot be negative.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Resource name cannot be empty.");
        }
        this.name = name;
    }

    public ResourceType getType() {
        return type;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(double availableQuantity) {
        this.availableQuantity = Math.max(0, availableQuantity);
    }

    public String getUnit() {
        return unit;
    }

    public boolean allocate(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Allocation quantity must be positive.");
        }
        if (availableQuantity < quantity) {
            return false;
        }
        availableQuantity -= quantity;
        return true;
    }

    public void replenish(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Replenishment quantity must be positive.");
        }
        availableQuantity = Math.min(totalQuantity, availableQuantity + quantity);
    }
}
