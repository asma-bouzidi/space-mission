package com.spacemission.model;

public class Satellite {
    private int id;
    private String name;
    private String orbitType;
    private int launchYear;

    public Satellite() {
    }

    public Satellite(int id, String name, String orbitType, int launchYear) {
        this.id = id;
        this.name = name;
        this.orbitType = orbitType;
        this.launchYear = launchYear;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrbitType() {
        return orbitType;
    }

    public void setOrbitType(String orbitType) {
        this.orbitType = orbitType;
    }

    public int getLaunchYear() {
        return launchYear;
    }

    public void setLaunchYear(int launchYear) {
        this.launchYear = launchYear;
    }

    @Override
    public String toString() {
        return "Satellite{id=" + id +
                ", name='" + name + '\'' +
                ", orbitType='" + orbitType + '\'' +
                ", launchYear=" + launchYear + '}';
    }
}
