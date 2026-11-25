package com.spacemission.service.communication;

import com.spacemission.dao.impl.JdbcCommunicationLogDao;
import com.spacemission.dao.interfaces.CommunicationLogDao;
import com.spacemission.exception.CommunicationException;
import com.spacemission.model.communication.CommunicationLog;

import java.util.List;
import java.util.Objects;

public class CommunicationService {
    private final CommunicationLogDao communicationLogDao;

    public CommunicationService() {
        this(new JdbcCommunicationLogDao());
    }

    public CommunicationService(CommunicationLogDao communicationLogDao) {
        this.communicationLogDao = Objects.requireNonNull(communicationLogDao, "Communication DAO cannot be null.");
    }

    public String sendCommand(String target, String command) {
        if (target == null || target.isBlank()) {
            throw new CommunicationException("Target cannot be empty.");
        }
        if (command == null || command.isBlank()) {
            throw new CommunicationException("Command cannot be empty.");
        }
        return "Command '" + command + "' sent to " + target + ".";
    }

    public CommunicationLog createLog(CommunicationLog log) {
        if (log == null) {
            throw new IllegalArgumentException("Communication log cannot be null.");
        }
        if (log.getSpacecraftId() <= 0 && log.getMissionId() <= 0) {
            throw new IllegalArgumentException("At least one related entity (spacecraft or mission) is required.");
        }
        return communicationLogDao.save(log);
    }

    public CommunicationLog findById(long id) {
        return communicationLogDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Communication log not found: " + id));
    }

    public List<CommunicationLog> findAll() {
        return communicationLogDao.findAll();
    }

    public List<CommunicationLog> findBySpacecraftId(int spacecraftId) {
        if (spacecraftId <= 0) {
            throw new IllegalArgumentException("Spacecraft id must be positive.");
        }
        return communicationLogDao.findBySpacecraftId(spacecraftId);
    }

    public void updateLog(CommunicationLog log) {
        if (log == null) {
            throw new IllegalArgumentException("Communication log cannot be null.");
        }
        if (log.getId() <= 0) {
            throw new IllegalArgumentException("Communication log id is required for updates.");
        }
        communicationLogDao.update(log);
    }

    public void deleteLog(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Communication log id must be positive.");
        }
        communicationLogDao.deleteById(id);
    }
}
