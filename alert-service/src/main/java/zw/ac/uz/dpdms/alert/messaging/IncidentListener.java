package zw.ac.uz.dpdms.alert.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zw.ac.uz.dpdms.alert.service.AlertService;

@Component
public class IncidentListener {

    private final AlertService alerts;
    @Value("${alerts.email.coordinator:coordinator@dpdms.local}") private String coordinatorEmail;
    @Value("${alerts.whatsapp.ops:+263000000000}") private String opsWhatsApp;

    public IncidentListener(AlertService alerts) { this.alerts = alerts; }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onApproved(IncidentApprovedEvent e) {
        if (e.floodThresholdExceeded()) {
            alerts.send(e, "EMAIL", coordinatorEmail, "FLOOD THRESHOLD EXCEEDED at " + e.ward());
            alerts.send(e, "WHATSAPP", opsWhatsApp, "Flood threshold exceeded at " + e.ward());
        }
        if (e.fireActive()) {
            alerts.send(e, "WHATSAPP", opsWhatsApp, "Active fire at " + e.ward());
        }
        if (e.casualties()) {
            alerts.send(e, "EMAIL", coordinatorEmail, "Casualties reported at " + e.ward());
        }
    }
}