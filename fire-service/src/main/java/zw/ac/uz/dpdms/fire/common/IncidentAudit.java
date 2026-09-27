package zw.ac.uz.dpdms.fire.common;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incident_audit",
    indexes = { @Index(name = "idx_audit_incident", columnList = "incidentId") })
public class IncidentAudit {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false) private UUID incidentId;
    @Column(nullable = false) private String actorUsername;

    @Enumerated(EnumType.STRING) private IncidentStatus previousStatus;
    @Enumerated(EnumType.STRING) private IncidentStatus newStatus;

    @Column(length = 1000) private String action;
    @Column(nullable = false) private LocalDateTime timestamp;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getIncidentId() { return incidentId; }
    public void setIncidentId(UUID incidentId) { this.incidentId = incidentId; }
    public String getActorUsername() { return actorUsername; }
    public void setActorUsername(String actorUsername) { this.actorUsername = actorUsername; }
    public IncidentStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(IncidentStatus previousStatus) { this.previousStatus = previousStatus; }
    public IncidentStatus getNewStatus() { return newStatus; }
    public void setNewStatus(IncidentStatus newStatus) { this.newStatus = newStatus; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}