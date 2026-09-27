package zw.ac.uz.dpdms.alert.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import zw.ac.uz.dpdms.alert.domain.AlertLog;
import zw.ac.uz.dpdms.alert.messaging.IncidentApprovedEvent;
import zw.ac.uz.dpdms.alert.repo.AlertLogRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);
    private final AlertLogRepository repo;
    private final JavaMailSender mail;
    private final RestTemplate rest = new RestTemplate();

    @Value("${spring.mail.username:no-reply@dpdms.local}") private String mailFrom;
    @Value("${whatsapp.token:}") private String waToken;
    @Value("${whatsapp.phoneId:}") private String waPhoneId;

    public AlertService(AlertLogRepository repo, JavaMailSender mail) {
        this.repo = repo; this.mail = mail;
    }

    public void send(IncidentApprovedEvent evt, String channel, String recipient, String message) {
        AlertLog l = new AlertLog();
        l.setIncidentId(evt.incidentId());
        l.setChannel(channel);
        l.setRecipient(recipient);
        l.setTimestamp(LocalDateTime.now());
        l.setMessage(message);
        try {
            switch (channel) {
                case "EMAIL" -> sendEmail(recipient, "DPDMS Alert - " + evt.hazard(), message);
                case "WHATSAPP" -> sendWhatsApp(recipient, message);
                default -> throw new IllegalArgumentException("Unknown channel " + channel);
            }
            l.setDeliveryStatus("SENT");
        } catch (Exception e) {
            l.setDeliveryStatus("FAILED");
            l.setErrorMessage(e.getMessage());
            log.warn("Alert delivery failed: {}", e.getMessage());
        }
        repo.save(l);
    }

    private void sendEmail(String to, String subject, String body) {
        if (mailFrom == null || mailFrom.isBlank())
            throw new IllegalStateException("Email credentials not configured");
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom(mailFrom); m.setTo(to); m.setSubject(subject); m.setText(body);
        mail.send(m);
    }

    private void sendWhatsApp(String to, String message) {
        if (waToken == null || waToken.isBlank() || waPhoneId == null || waPhoneId.isBlank())
            throw new IllegalStateException("WhatsApp credentials not configured");
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(waToken);
        h.setContentType(MediaType.APPLICATION_JSON);
        Map<String,Object> payload = Map.of(
            "messaging_product", "whatsapp",
            "to", to,
            "type", "text",
            "text", Map.of("body", message));
        ResponseEntity<String> resp = rest.postForEntity(
            "https://graph.facebook.com/v19.0/" + waPhoneId + "/messages",
            new HttpEntity<>(payload, h), String.class);
        if (!resp.getStatusCode().is2xxSuccessful())
            throw new IllegalStateException("WhatsApp API " + resp.getStatusCode());
    }
}