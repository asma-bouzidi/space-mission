package com.spacemission.dao.interfaces;

import com.spacemission.model.mission.MissionTeam;

import java.util.List;
import java.util.Optional;

public interface MissionTeamDao {
    MissionTeam save(MissionTeam missionTeam);

    Optional<MissionTeam> findById(int id);

    List<MissionTeam> findAll();

    List<MissionTeam> findByMissionId(int missionId);

    List<MissionTeam> findByUserId(int userId);

    void update(MissionTeam missionTeam);

    void deleteById(int id);
}
