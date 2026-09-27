package zw.ac.uz.dpdms.drought.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.ac.uz.dpdms.drought.common.*;
import zw.ac.uz.dpdms.drought.domain.DroughtIncident;
import zw.ac.uz.dpdms.drought.dto.DroughtRequest;
import zw.ac.uz.dpdms.drought.dto.DroughtResponse;
import zw.ac.uz.dpdms.drought.repo.DroughtIncidentRepository;
import zw.ac.uz.dpdms.drought.repo.IncidentAuditRepository;
import zw.ac.uz.dpdms.drought.security.ScopeGuard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DroughtIncidentService {

    private final DroughtIncidentRepository repo;
    private final IncidentAuditRepository auditRepo;
    private final ScopeGuard guard;
    private final RabbitTemplate rabbit;

    public DroughtIncidentService(DroughtIncidentRepository repo, IncidentAuditRepository auditRepo,
                                  ScopeGuard guard, RabbitTemplate rabbit) {
        this.repo = repo; this.auditRepo = auditRepo; this.guard = guard; this.rabbit = rabbit;
    }

    @Transactional
    public DroughtResponse create(DroughtRequest r) {
        guard.requireNotNationalForWrite();
        guard.requireRecorderOwnsWard(r.ward());
        DroughtIncident e = new DroughtIncident();
        e.setWard(r.ward()); e.setDistrict(r.district()); e.setProvince("Rushinga");
        e.setOccurredAt(r.occurredAt()); e.setSeverity(r.severity());
        e.setLatitude(r.latitude()); e.setLongitude(r.longitude());
        e.setReporterUsername(guard.username());
        e.setStatus(IncidentStatus.PENDING);
        e.setRainfallDeficitMm(r.rainfallDeficitMm());
        e.setConsecutiveDryDays(r.consecutiveDryDays());
        e.setCropFailurePct(r.cropFailurePct());
        e.setPeopleWaterShortage(r.peopleWaterShortage());
        e.setLivestockMortality(r.livestockMortality());
        e = repo.save(e);
        audit(e.getId(), null, IncidentStatus.PENDING, "Created by recorder");
        return DroughtResponse.from(e);
    }

    public DroughtResponse get(UUID id) {
        DroughtIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireHazardRead();
        if (e.getStatus() != IncidentStatus.APPROVED
            && !guard.role().equals("DROUGHT_SUPERVISOR")
            && !guard.role().equals("PROVINCIAL_ADMIN")
            && !(guard.role().equals("DROUGHT_RECORDER") && e.getReporterUsername().equals(guard.username())))
            throw new ApiException(403, "You cannot view a non-approved incident");
        guard.requireCanReadWard(e.getWard());
        return DroughtResponse.from(e);
    }

    public List<DroughtResponse> list() {
        guard.requireHazardRead();
        List<DroughtIncident> rows = switch (guard.role()) {
            case "DROUGHT_RECORDER" -> repo.findByWard(guard.ward());
            case "DROUGHT_SUPERVISOR", "PROVINCIAL_ADMIN" -> repo.findAll();
            case "NATIONAL_USER" -> repo.findByStatus(IncidentStatus.APPROVED);
            default -> throw new ApiException(403, "Not authorized");
        };
        return rows.stream().map(DroughtResponse::from).toList();
    }

    @Transactional
    public DroughtResponse update(UUID id, DroughtRequest r) {
        guard.requireNotNationalForWrite();
        DroughtIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireRecorderOwnsWard(e.getWard());
        if (!e.getReporterUsername().equals(guard.username()))
            throw new ApiException(403, "You can only edit your own incidents");
        if (e.getStatus() == IncidentStatus.APPROVED || e.getStatus() == IncidentStatus.REJECTED)
            throw new ApiException(409, "Cannot modify an incident in status " + e.getStatus());
        e.setWard(r.ward()); e.setDistrict(r.district());
        e.setOccurredAt(r.occurredAt()); e.setSeverity(r.severity());
        e.setLatitude(r.latitude()); e.setLongitude(r.longitude());
        e.setRainfallDeficitMm(r.rainfallDeficitMm());
        e.setConsecutiveDryDays(r.consecutiveDryDays());
        e.setCropFailurePct(r.cropFailurePct());
        e.setPeopleWaterShortage(r.peopleWaterShortage());
        e.setLivestockMortality(r.livestockMortality());
        if (e.getStatus() == IncidentStatus.CORRECTION_REQUIRED) {
            audit(e.getId(), IncidentStatus.CORRECTION_REQUIRED, IncidentStatus.PENDING, "Resubmitted");
            e.setStatus(IncidentStatus.PENDING);
        }
        return DroughtResponse.from(repo.save(e));
    }

    @Transactional
    public void delete(UUID id) {
        guard.requireNotNationalForWrite();
        DroughtIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireRecorderOwnsWard(e.getWard());
        if (!e.getReporterUsername().equals(guard.username()))
            throw new ApiException(403, "You can only delete your own incidents");
        if (e.getStatus() == IncidentStatus.APPROVED)
            throw new ApiException(409, "Approved incidents cannot be deleted");
        repo.delete(e);
    }

    @Transactional
    public DroughtResponse approve(UUID id) {
        guard.requireSupervisor();
        DroughtIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.APPROVED, "Approved");
        e.setStatus(IncidentStatus.APPROVED);
        e = repo.save(e);
        publish(e);
        return DroughtResponse.from(e);
    }

    @Transactional
    public DroughtResponse reject(UUID id, String reason) {
        guard.requireSupervisor();
        DroughtIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.REJECTED, reason);
        e.setStatus(IncidentStatus.REJECTED);
        e.setRejectionReason(reason);
        return DroughtResponse.from(repo.save(e));
    }

    @Transactional
    public DroughtResponse requestCorrection(UUID id, String reason) {
        guard.requireSupervisor();
        DroughtIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.CORRECTION_REQUIRED, reason);
        e.setStatus(IncidentStatus.CORRECTION_REQUIRED);
        e.setRejectionReason(reason);
        return DroughtResponse.from(repo.save(e));
    }

    public List<IncidentAudit> audits(UUID id) {
        guard.requireHazardRead();
        return auditRepo.findByIncidentIdOrderByTimestampAsc(id);
    }

    private DroughtIncident loadPending(UUID id) {
        DroughtIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
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

    private void publish(DroughtIncident e) {
        try {
            rabbit.convertAndSend("dpdms.incidents", "incident.approved",
                new IncidentApprovedEvent(e.getId(), "DROUGHT", e.getWard(), e.getDistrict(),
                    e.getSeverity().name(), e.getLatitude(), e.getLongitude(),
                    false, false, false));
        } catch (Exception ignored) {}
    }
}