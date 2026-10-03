package zw.ac.uz.dpdms.alert.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zw.ac.uz.dpdms.alert.service.AlertService;

@Component
public class IncidentListener {

    private static final Logger log = LoggerFactory.getLogger(IncidentListener.class);

    private final AlertService alerts;

    @Value("${alerts.email.coordinator:coordinator@dpdms.local}")
    private String coordinatorEmail;

    @Value("${alerts.whatsapp.ops:+263000000000}")
    private String opsWhatsApp;

    public IncidentListener(AlertService alerts) { this.alerts = alerts; }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onApproved(IncidentApprovedEvent e) {
        log.info("Received approved incident: hazard={} ward={} severity={}",
                e.hazard(), e.ward(), e.severity());

        // Always send an alert for every approved hazard
        String subject = "DPDMS ALERT — " + e.hazard() + " approved in " + e.ward();
        String body = buildBody(e);

        // Send via email (always)
        try {
            alerts.send(e, "EMAIL", coordinatorEmail, subject + "\n\n" + body);
        } catch (Exception ex) {
            log.warn("EMAIL send failed: {}", ex.getMessage());
        }

        // Send via WhatsApp (always)
        try {
            alerts.send(e, "WHATSAPP", opsWhatsApp, body);
        } catch (Exception ex) {
            log.warn("WHATSAPP send failed: {}", ex.getMessage());
        }

        // Extra escalation alerts for specific conditions
        if (e.floodThresholdExceeded()) {
            alerts.send(e, "EMAIL", coordinatorEmail,
                "[ESCALATION] FLOOD THRESHOLD EXCEEDED at " + e.ward() +
                " — peak water level above 5.0m");
            alerts.send(e, "WHATSAPP", opsWhatsApp,
                "FLOOD THRESHOLD EXCEEDED at " + e.ward());
        }

        if (e.fireActive()) {
            alerts.send(e, "WHATSAPP", opsWhatsApp,
                "[ESCALATION] Active fire still burning at " + e.ward());
        }

        if (e.casualties()) {
            alerts.send(e, "EMAIL", coordinatorEmail,
                "[ESCALATION] Casualties reported at " + e.ward() +
                " — " + e.hazard());
        }
    }

    private String buildBody(IncidentApprovedEvent e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hazard:   ").append(e.hazard()).append("\n");
        sb.append("Ward:     ").append(e.ward()).append("\n");
        sb.append("District: ").append(e.district()).append("\n");
        sb.append("Severity: ").append(e.severity()).append("\n");
        if (e.latitude() != null && e.longitude() != null) {
            sb.append("Location: ").append(e.latitude()).append(", ").append(e.longitude()).append("\n");
        }
        sb.append("\nThis incident has been APPROVED and is now visible on the DPDMS dashboard.\n");
        sb.append("Log in at http://localhost:3000 for details.");
        return sb.toString();
    }
}