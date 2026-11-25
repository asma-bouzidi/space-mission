package com.spacemission.service.mission;

import com.spacemission.dao.impl.JdbcMissionTeamDao;
import com.spacemission.dao.interfaces.MissionTeamDao;
import com.spacemission.model.mission.MissionTeam;

import java.util.List;
import java.util.Objects;

public class MissionTeamService {
    private final MissionTeamDao missionTeamDao;

    public MissionTeamService() {
        this(new JdbcMissionTeamDao());
    }

    public MissionTeamService(MissionTeamDao missionTeamDao) {
        this.missionTeamDao = Objects.requireNonNull(missionTeamDao, "Mission team DAO cannot be null.");
    }

    public MissionTeam create(MissionTeam missionTeam) {
        validate(missionTeam);
        return missionTeamDao.save(missionTeam);
    }

    public MissionTeam findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Mission team id must be positive.");
        }
        return missionTeamDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mission team member not found: " + id));
    }

    public List<MissionTeam> findAll() {
        return missionTeamDao.findAll();
    }

    public List<MissionTeam> findByMissionId(int missionId) {
        if (missionId <= 0) {
            throw new IllegalArgumentException("Mission id must be positive.");
        }
        return missionTeamDao.findByMissionId(missionId);
    }

    public List<MissionTeam> findByUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User id must be positive.");
        }
        return missionTeamDao.findByUserId(userId);
    }

    public List<MissionTeam> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        return findAll().stream()
                .filter(item -> item.getRoleName().toLowerCase().contains(keyword.toLowerCase())
                        || (String.valueOf(item.getMissionId()).contains(keyword)
                        || String.valueOf(item.getUserId()).contains(keyword)))
                .toList();
    }

    public void update(MissionTeam missionTeam) {
        validate(missionTeam);
        if (missionTeam.getId() <= 0) {
            throw new IllegalArgumentException("Mission team id is required for updates.");
        }
        missionTeamDao.update(missionTeam);
    }

    public void deleteById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Mission team id must be positive.");
        }
        missionTeamDao.deleteById(id);
    }

    private void validate(MissionTeam missionTeam) {
        if (missionTeam == null) {
            throw new IllegalArgumentException("Mission team member cannot be null.");
        }
        if (missionTeam.getMissionId() <= 0) {
            throw new IllegalArgumentException("Mission team member must belong to a valid mission.");
        }
        if (missionTeam.getUserId() <= 0) {
            throw new IllegalArgumentException("Mission team member must belong to a valid user.");
        }
        if (missionTeam.getRoleName() == null || missionTeam.getRoleName().isBlank()) {
            throw new IllegalArgumentException("Mission team role name is required.");
        }
    }
}
