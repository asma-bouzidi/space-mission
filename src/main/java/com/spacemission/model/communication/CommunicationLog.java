package com.spacemission.model.communication;

import java.time.LocalDateTime;

public class CommunicationLog {
    private int id;
    private int spacecraftId;
    private int missionId;
    private String sender;
    private String recipient;
    private String commandType;
    private String payload;
    private String status;
    private LocalDateTime sentAt;

    public CommunicationLog(int id, int spacecraftId, int missionId, String sender, String recipient,
                            String commandType, String payload, String status) {
        this(id, spacecraftId, missionId, sender, recipient, commandType, payload, status, LocalDateTime.now());
    }

    public CommunicationLog(int id, int spacecraftId, String sender, String recipient, String commandType,
                            String payload, String status) {
        this(id, spacecraftId, 0, sender, recipient, commandType, payload, status);
    }

    public CommunicationLog(int id, int spacecraftId, int missionId, String sender, String recipient,
                            String commandType, String payload, String status, LocalDateTime sentAt) {
        this.id = id;
        this.spacecraftId = spacecraftId;
        this.missionId = missionId;
        this.sender = sender == null || sender.isBlank() ? "SYSTEM" : sender;
        this.recipient = recipient == null || recipient.isBlank() ? "MISSION_CONTROL" : recipient;
        this.commandType = commandType == null || commandType.isBlank() ? "PING" : commandType;
        this.payload = payload == null ? "" : payload;
        this.status = status == null || status.isBlank() ? "SENT" : status;
        this.sentAt = sentAt == null ? LocalDateTime.now() : sentAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpacecraftId() {
        return spacecraftId;
    }

    public void setSpacecraftId(int spacecraftId) {
        this.spacecraftId = spacecraftId;
    }

    public int getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        this.missionId = missionId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender == null || sender.isBlank() ? "SYSTEM" : sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient == null || recipient.isBlank() ? "MISSION_CONTROL" : recipient;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType == null || commandType.isBlank() ? "PING" : commandType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload == null ? "" : payload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null || status.isBlank() ? "SENT" : status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt == null ? LocalDateTime.now() : sentAt;
    }
}
