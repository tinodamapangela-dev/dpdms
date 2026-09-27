package zw.ac.uz.dpdms.alert.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alert_logs", indexes = { @Index(name = "idx_alert_incident", columnList = "incidentId") })
public class AlertLog {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID incidentId;
    @Column(nullable = false) private String channel;
    @Column(nullable = false) private String recipient;
    @Column(nullable = false) private LocalDateTime timestamp;
    @Column(nullable = false) private String deliveryStatus;
    @Column(length = 1000) private String errorMessage;
    @Column(length = 1000) private String message;

    public UUID getId() { return id; } public void setId(UUID v) { this.id = v; }
    public UUID getIncidentId() { return incidentId; } public void setIncidentId(UUID v) { this.incidentId = v; }
    public String getChannel() { return channel; } public void setChannel(String v) { this.channel = v; }
    public String getRecipient() { return recipient; } public void setRecipient(String v) { this.recipient = v; }
    public LocalDateTime getTimestamp() { return timestamp; } public void setTimestamp(LocalDateTime v) { this.timestamp = v; }
    public String getDeliveryStatus() { return deliveryStatus; } public void setDeliveryStatus(String v) { this.deliveryStatus = v; }
    public String getErrorMessage() { return errorMessage; } public void setErrorMessage(String v) { this.errorMessage = v; }
    public String getMessage() { return message; } public void setMessage(String v) { this.message = v; }
}