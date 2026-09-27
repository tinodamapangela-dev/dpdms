package zw.ac.uz.dpdms.fire.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.ac.uz.dpdms.fire.common.*;
import zw.ac.uz.dpdms.fire.domain.FireIncident;
import zw.ac.uz.dpdms.fire.dto.FireRequest;
import zw.ac.uz.dpdms.fire.dto.FireResponse;
import zw.ac.uz.dpdms.fire.repo.FireIncidentRepository;
import zw.ac.uz.dpdms.fire.repo.IncidentAuditRepository;
import zw.ac.uz.dpdms.fire.security.ScopeGuard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FireIncidentService {

    private final FireIncidentRepository repo;
    private final IncidentAuditRepository auditRepo;
    private final ScopeGuard guard;
    private final RabbitTemplate rabbit;

    public FireIncidentService(FireIncidentRepository repo, IncidentAuditRepository auditRepo,
                               ScopeGuard guard, RabbitTemplate rabbit) {
        this.repo = repo; this.auditRepo = auditRepo; this.guard = guard; this.rabbit = rabbit;
    }

    @Transactional
    public FireResponse create(FireRequest r) {
        guard.requireNotNationalForWrite();
        guard.requireRecorderOwnsWard(r.ward());
        FireIncident e = new FireIncident();
        e.setWard(r.ward()); e.setDistrict(r.district()); e.setProvince("Rushinga");
        e.setOccurredAt(r.occurredAt()); e.setSeverity(r.severity());
        e.setLatitude(r.latitude()); e.setLongitude(r.longitude());
        e.setReporterUsername(guard.username());
        e.setStatus(IncidentStatus.PENDING);
        e.setAreaBurnedHa(r.areaBurnedHa());
        e.setSuspectedCause(r.suspectedCause());
        e.setInjuries(r.injuries());
        e.setFatalities(r.fatalities());
        e.setStructuresDestroyed(r.structuresDestroyed());
        e.setActive(r.active());
        e = repo.save(e);
        audit(e.getId(), null, IncidentStatus.PENDING, "Created by recorder");
        return FireResponse.from(e);
    }

    public FireResponse get(UUID id) {
        FireIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireHazardRead();
        if (e.getStatus() != IncidentStatus.APPROVED
            && !guard.role().equals("FIRE_SUPERVISOR")
            && !guard.role().equals("PROVINCIAL_ADMIN")
            && !(guard.role().equals("FIRE_RECORDER") && e.getReporterUsername().equals(guard.username())))
            throw new ApiException(403, "You cannot view a non-approved incident");
        guard.requireCanReadWard(e.getWard());
        return FireResponse.from(e);
    }

    public List<FireResponse> list() {
        guard.requireHazardRead();
        List<FireIncident> rows = switch (guard.role()) {
            case "FIRE_RECORDER" -> repo.findByWard(guard.ward());
            case "FIRE_SUPERVISOR", "PROVINCIAL_ADMIN" -> repo.findAll();
            case "NATIONAL_USER" -> repo.findByStatus(IncidentStatus.APPROVED);
            default -> throw new ApiException(403, "Not authorized");
        };
        return rows.stream().map(FireResponse::from).toList();
    }

    @Transactional
    public FireResponse update(UUID id, FireRequest r) {
        guard.requireNotNationalForWrite();
        FireIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireRecorderOwnsWard(e.getWard());
        if (!e.getReporterUsername().equals(guard.username()))
            throw new ApiException(403, "You can only edit your own incidents");
        if (e.getStatus() == IncidentStatus.APPROVED || e.getStatus() == IncidentStatus.REJECTED)
            throw new ApiException(409, "Cannot modify an incident in status " + e.getStatus());
        e.setWard(r.ward()); e.setDistrict(r.district());
        e.setOccurredAt(r.occurredAt()); e.setSeverity(r.severity());
        e.setLatitude(r.latitude()); e.setLongitude(r.longitude());
        e.setAreaBurnedHa(r.areaBurnedHa());
        e.setSuspectedCause(r.suspectedCause());
        e.setInjuries(r.injuries());
        e.setFatalities(r.fatalities());
        e.setStructuresDestroyed(r.structuresDestroyed());
        e.setActive(r.active());
        if (e.getStatus() == IncidentStatus.CORRECTION_REQUIRED) {
            audit(e.getId(), IncidentStatus.CORRECTION_REQUIRED, IncidentStatus.PENDING, "Resubmitted");
            e.setStatus(IncidentStatus.PENDING);
        }
        return FireResponse.from(repo.save(e));
    }

    @Transactional
    public void delete(UUID id) {
        guard.requireNotNationalForWrite();
        FireIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireRecorderOwnsWard(e.getWard());
        if (!e.getReporterUsername().equals(guard.username()))
            throw new ApiException(403, "You can only delete your own incidents");
        if (e.getStatus() == IncidentStatus.APPROVED)
            throw new ApiException(409, "Approved incidents cannot be deleted");
        repo.delete(e);
    }

    @Transactional
    public FireResponse approve(UUID id) {
        guard.requireSupervisor();
        FireIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.APPROVED, "Approved");
        e.setStatus(IncidentStatus.APPROVED);
        e = repo.save(e);
        publish(e);
        return FireResponse.from(e);
    }

    @Transactional
    public FireResponse reject(UUID id, String reason) {
        guard.requireSupervisor();
        FireIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.REJECTED, reason);
        e.setStatus(IncidentStatus.REJECTED);
        e.setRejectionReason(reason);
        return FireResponse.from(repo.save(e));
    }

    @Transactional
    public FireResponse requestCorrection(UUID id, String reason) {
        guard.requireSupervisor();
        FireIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.CORRECTION_REQUIRED, reason);
        e.setStatus(IncidentStatus.CORRECTION_REQUIRED);
        e.setRejectionReason(reason);
        return FireResponse.from(repo.save(e));
    }

    public List<IncidentAudit> audits(UUID id) {
        guard.requireHazardRead();
        return auditRepo.findByIncidentIdOrderByTimestampAsc(id);
    }

    private FireIncident loadPending(UUID id) {
        FireIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
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

    private void publish(FireIncident e) {
        try {
            boolean hasCasualties = (e.getFatalities() != null && e.getFatalities() > 0);
            rabbit.convertAndSend("dpdms.incidents", "incident.approved",
                new IncidentApprovedEvent(e.getId(), "FIRE", e.getWard(), e.getDistrict(),
                    e.getSeverity().name(), e.getLatitude(), e.getLongitude(),
                    false, Boolean.TRUE.equals(e.getActive()), hasCasualties));
        } catch (Exception ignored) {}
    }
}