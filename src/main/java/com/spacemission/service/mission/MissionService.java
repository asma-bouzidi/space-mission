package com.spacemission.service.mission;

import com.spacemission.config.DatabaseTransactionManager;
import com.spacemission.dao.impl.JdbcMissionDao;
import com.spacemission.dao.interfaces.MissionDao;
import com.spacemission.enums.MissionStatus;
import com.spacemission.exception.InvalidMissionStateException;
import com.spacemission.exception.MissionConflictException;
import com.spacemission.model.mission.Mission;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MissionService {
    private final MissionDao missionDao;
    private final List<Mission> missions = new ArrayList<>();

    public MissionService() {
        this(null);
    }

    public MissionService(MissionDao missionDao) {
        this.missionDao = missionDao;
    }

    public void createMission(Mission mission) {
        Objects.requireNonNull(mission, "Mission cannot be null.");
        if (mission.getId() > 0 && missions.stream().anyMatch(m -> m.getId() == mission.getId())) {
            throw new MissionConflictException("Mission already exists: " + mission.getId());
        }
        if (missionDao != null) {
            missionDao.save(mission);
            missions.clear();
            missions.addAll(missionDao.findAll());
            return;
        }

        if (missions.stream().anyMatch(m -> m.getMissionCode().equalsIgnoreCase(mission.getMissionCode()))) {
            throw new MissionConflictException("Mission code already exists: " + mission.getMissionCode());
        }
        missions.add(mission);
    }

    public void updateMission(Mission mission) {
        Objects.requireNonNull(mission, "Mission cannot be null.");
        if (missionDao != null) {
            missionDao.update(mission);
            missions.clear();
            missions.addAll(missionDao.findAll());
            return;
        }
        Mission existing = findById(mission.getId());
        existing.setMissionCode(mission.getMissionCode());
        existing.setName(mission.getName());
        existing.setDescription(mission.getDescription());
        existing.setMissionType(mission.getMissionType());
        existing.setPriority(mission.getPriority());
        existing.setStatus(mission.getStatus());
        existing.setStartDate(mission.getStartDate());
        existing.setExpectedEndDate(mission.getExpectedEndDate());
        existing.setActualEndDate(mission.getActualEndDate());
    }

    public void deleteMission(int missionId) {
        if (missionDao != null) {
            missionDao.deleteById(missionId);
            missions.clear();
            missions.addAll(missionDao.findAll());
            return;
        }
        Mission mission = findById(missionId);
        missions.remove(mission);
    }

    public void updateMissionStatus(int missionId, MissionStatus newStatus) {
        Mission mission = findById(missionId);
        mission.updateStatus(newStatus);
        if (missionDao != null) {
            missionDao.update(mission);
            missions.clear();
            missions.addAll(missionDao.findAll());
        }
    }

    public Mission findById(int missionId) {
        if (missionDao != null) {
            return missionDao.findById(missionId)
                    .orElseThrow(() -> new IllegalArgumentException("Mission not found: " + missionId));
        }
        return missions.stream()
                .filter(mission -> mission.getId() == missionId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Mission not found: " + missionId));
    }

    public List<Mission> getAll() {
        if (missionDao != null) {
            return missionDao.findAll();
        }
        return new ArrayList<>(missions);
    }

    public List<Mission> findByStatus(MissionStatus status) {
        Objects.requireNonNull(status, "Status cannot be null.");
        if (missionDao != null) {
            return missionDao.findByStatus(status.name());
        }
        return missions.stream()
                .filter(mission -> mission.getStatus() == status)
                .toList();
    }

    public void assignSpacecraftToMission(int missionId, int spacecraftId) {
        Mission mission = findById(missionId);
        mission.assignSpacecraft(spacecraftId);
        if (missionDao != null) {
            missionDao.update(mission);
            missions.clear();
            missions.addAll(missionDao.findAll());
        }
    }

    public void assignSpacecraftToMissionTransactional(int missionId, int spacecraftId) {
        if (missionDao == null) {
            throw new IllegalStateException("Mission DAO is not configured for transactional assignment.");
        }

        DatabaseTransactionManager.executeInTransaction(connection -> {
            Mission currentMission = missionDao.findById(missionId, connection)
                    .orElseThrow(() -> new IllegalArgumentException("Mission not found: " + missionId));
            currentMission.assignSpacecraft(spacecraftId);
            missionDao.update(currentMission, connection);
            return null;
        });
    }
}
