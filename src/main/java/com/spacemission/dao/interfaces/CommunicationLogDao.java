package com.spacemission.dao.interfaces;

import com.spacemission.model.communication.CommunicationLog;

import java.util.List;
import java.util.Optional;

public interface CommunicationLogDao {
    CommunicationLog save(CommunicationLog log);

    Optional<CommunicationLog> findById(long id);

    List<CommunicationLog> findAll();

    List<CommunicationLog> findBySpacecraftId(int spacecraftId);

    void update(CommunicationLog log);

    void deleteById(long id);
}
