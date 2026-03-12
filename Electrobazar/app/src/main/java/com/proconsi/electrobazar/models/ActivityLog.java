package com.proconsi.electrobazar.models;

public class ActivityLog {
    private Long id;
    private String action;
    private String description;
    private String username;
    private String timestamp;
    private String entityType;
    private Long entityId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
}
