package zw.ac.uz.dpdms.zoonotic.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.ac.uz.dpdms.zoonotic.common.*;
import zw.ac.uz.dpdms.zoonotic.domain.Classification;
import zw.ac.uz.dpdms.zoonotic.domain.ZoonoticIncident;
import zw.ac.uz.dpdms.zoonotic.dto.ZoonoticRequest;
import zw.ac.uz.dpdms.zoonotic.dto.ZoonoticResponse;
import zw.ac.uz.dpdms.zoonotic.repo.IncidentAuditRepository;
import zw.ac.uz.dpdms.zoonotic.repo.ZoonoticIncidentRepository;
import zw.ac.uz.dpdms.zoonotic.security.ScopeGuard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ZoonoticIncidentService {

    private final ZoonoticIncidentRepository repo;
    private final IncidentAuditRepository auditRepo;
    private final ScopeGuard guard;
    private final RabbitTemplate rabbit;

    public ZoonoticIncidentService(ZoonoticIncidentRepository repo, IncidentAuditRepository auditRepo,
                                   ScopeGuard guard, RabbitTemplate rabbit) {
        this.repo = repo; this.auditRepo = auditRepo; this.guard = guard; this.rabbit = rabbit;
    }

    @Transactional
    public ZoonoticResponse create(ZoonoticRequest r) {
        guard.requireNotNationalForWrite();
        guard.requireRecorderOwnsWard(r.ward());
        ZoonoticIncident e = new ZoonoticIncident();
        e.setWard(r.ward()); e.setDistrict(r.district()); e.setProvince("Rushinga");
        e.setOccurredAt(r.occurredAt()); e.setSeverity(r.severity());
        e.setLatitude(r.latitude()); e.setLongitude(r.longitude());
        e.setReporterUsername(guard.username());
        e.setStatus(IncidentStatus.PENDING);
        e.setPathogen(r.pathogen());
        e.setAnimalSpecies(r.animalSpecies());
        e.setConfirmedHumanCases(r.confirmedHumanCases());
        e.setConfirmedAnimalCases(r.confirmedAnimalCases());
        e.setClassification(r.classification());
        e = repo.save(e);
        audit(e.getId(), null, IncidentStatus.PENDING, "Created by recorder");
        return ZoonoticResponse.from(e);
    }

    public ZoonoticResponse get(UUID id) {
        ZoonoticIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireHazardRead();
        if (e.getStatus() != IncidentStatus.APPROVED
            && !guard.role().equals("ZOONOTIC_SUPERVISOR")
            && !guard.role().equals("PROVINCIAL_ADMIN")
            && !(guard.role().equals("ZOONOTIC_RECORDER") && e.getReporterUsername().equals(guard.username())))
            throw new ApiException(403, "You cannot view a non-approved incident");
        guard.requireCanReadWard(e.getWard());
        return ZoonoticResponse.from(e);
    }

    public List<ZoonoticResponse> list() {
        guard.requireHazardRead();
        List<ZoonoticIncident> rows = switch (guard.role()) {
            case "ZOONOTIC_RECORDER" -> repo.findByWard(guard.ward());
            case "ZOONOTIC_SUPERVISOR", "PROVINCIAL_ADMIN" -> repo.findAll();
            case "NATIONAL_USER" -> repo.findByStatus(IncidentStatus.APPROVED);
            default -> throw new ApiException(403, "Not authorized");
        };
        return rows.stream().map(ZoonoticResponse::from).toList();
    }

    @Transactional
    public ZoonoticResponse update(UUID id, ZoonoticRequest r) {
        guard.requireNotNationalForWrite();
        ZoonoticIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireRecorderOwnsWard(e.getWard());
        if (!e.getReporterUsername().equals(guard.username()))
            throw new ApiException(403, "You can only edit your own incidents");
        if (e.getStatus() == IncidentStatus.APPROVED || e.getStatus() == IncidentStatus.REJECTED)
            throw new ApiException(409, "Cannot modify an incident in status " + e.getStatus());
        e.setWard(r.ward()); e.setDistrict(r.district());
        e.setOccurredAt(r.occurredAt()); e.setSeverity(r.severity());
        e.setLatitude(r.latitude()); e.setLongitude(r.longitude());
        e.setPathogen(r.pathogen());
        e.setAnimalSpecies(r.animalSpecies());
        e.setConfirmedHumanCases(r.confirmedHumanCases());
        e.setConfirmedAnimalCases(r.confirmedAnimalCases());
        e.setClassification(r.classification());
        if (e.getStatus() == IncidentStatus.CORRECTION_REQUIRED) {
            audit(e.getId(), IncidentStatus.CORRECTION_REQUIRED, IncidentStatus.PENDING, "Resubmitted");
            e.setStatus(IncidentStatus.PENDING);
        }
        return ZoonoticResponse.from(repo.save(e));
    }

    @Transactional
    public void delete(UUID id) {
        guard.requireNotNationalForWrite();
        ZoonoticIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireRecorderOwnsWard(e.getWard());
        if (!e.getReporterUsername().equals(guard.username()))
            throw new ApiException(403, "You can only delete your own incidents");
        if (e.getStatus() == IncidentStatus.APPROVED)
            throw new ApiException(409, "Approved incidents cannot be deleted");
        repo.delete(e);
    }

    @Transactional
    public ZoonoticResponse approve(UUID id) {
        guard.requireSupervisor();
        ZoonoticIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.APPROVED, "Approved");
        e.setStatus(IncidentStatus.APPROVED);
        e = repo.save(e);
        publish(e);
        return ZoonoticResponse.from(e);
    }

    @Transactional
    public ZoonoticResponse reject(UUID id, String reason) {
        guard.requireSupervisor();
        ZoonoticIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.REJECTED, reason);
        e.setStatus(IncidentStatus.REJECTED);
        e.setRejectionReason(reason);
        return ZoonoticResponse.from(repo.save(e));
    }

    @Transactional
    public ZoonoticResponse requestCorrection(UUID id, String reason) {
        guard.requireSupervisor();
        ZoonoticIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.CORRECTION_REQUIRED, reason);
        e.setStatus(IncidentStatus.CORRECTION_REQUIRED);
        e.setRejectionReason(reason);
        return ZoonoticResponse.from(repo.save(e));
    }

    public List<IncidentAudit> audits(UUID id) {
        guard.requireHazardRead();
        return auditRepo.findByIncidentIdOrderByTimestampAsc(id);
    }

    private ZoonoticIncident loadPending(UUID id) {
        ZoonoticIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        if (e.getStatus() != IncidentStatus.PENDING)
            throw new ApiException(409, "Only PENDING incidents can transition");
        return e;
    }

    private void audit(UUID incidentId, IncidentStatus from, IncidentStatus to, String action) {
        IncidentAudit a = new IncidentAudit();
        a.setIncidentId(incidentId);
        a.setActorUsername(guard.username());
        a.setPreviousStatus(from);
        a.setNewStatus(to);
        a.setAction(action);
        a.setTimestamp(LocalDateTime.now());
        auditRepo.save(a);
    }

    private void publish(ZoonoticIncident e) {
        try {
            boolean outbreak = e.getClassification() == Classification.OUTBREAK;
            rabbit.convertAndSend("dpdms.incidents", "incident.approved",
                new IncidentApprovedEvent(e.getId(), "ZOONOTIC_DISEASE", e.getWard(), e.getDistrict(),
                    e.getSeverity().name(), e.getLatitude(), e.getLongitude(),
                    false, false, outbreak));
        } catch (Exception ignored) {}
    }
}