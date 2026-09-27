package zw.ac.uz.dpdms.mining.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.ac.uz.dpdms.mining.common.*;
import zw.ac.uz.dpdms.mining.domain.MiningIncident;
import zw.ac.uz.dpdms.mining.dto.MiningRequest;
import zw.ac.uz.dpdms.mining.dto.MiningResponse;
import zw.ac.uz.dpdms.mining.repo.IncidentAuditRepository;
import zw.ac.uz.dpdms.mining.repo.MiningIncidentRepository;
import zw.ac.uz.dpdms.mining.security.ScopeGuard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MiningIncidentService {

    private final MiningIncidentRepository repo;
    private final IncidentAuditRepository auditRepo;
    private final ScopeGuard guard;
    private final RabbitTemplate rabbit;

    public MiningIncidentService(MiningIncidentRepository repo, IncidentAuditRepository auditRepo,
                                 ScopeGuard guard, RabbitTemplate rabbit) {
        this.repo = repo; this.auditRepo = auditRepo; this.guard = guard; this.rabbit = rabbit;
    }

    @Transactional
    public MiningResponse create(MiningRequest r) {
        guard.requireNotNationalForWrite();
        guard.requireRecorderOwnsWard(r.ward());
        MiningIncident e = new MiningIncident();
        e.setWard(r.ward()); e.setDistrict(r.district()); e.setProvince("Rushinga");
        e.setOccurredAt(r.occurredAt()); e.setSeverity(r.severity());
        e.setLatitude(r.latitude()); e.setLongitude(r.longitude());
        e.setReporterUsername(guard.username());
        e.setStatus(IncidentStatus.PENDING);
        e.setMineName(r.mineName());
        e.setMineType(r.mineType());
        e.setAccidentType(r.accidentType());
        e.setTrappedMiners(r.trappedMiners());
        e.setInjuredMiners(r.injuredMiners());
        e.setFatalities(r.fatalities());
        e.setRescueOngoing(r.rescueOngoing());
        e = repo.save(e);
        audit(e.getId(), null, IncidentStatus.PENDING, "Created by recorder");
        return MiningResponse.from(e);
    }

    public MiningResponse get(UUID id) {
        MiningIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireHazardRead();
        if (e.getStatus() != IncidentStatus.APPROVED
            && !guard.role().equals("MINING_SUPERVISOR")
            && !guard.role().equals("PROVINCIAL_ADMIN")
            && !(guard.role().equals("MINING_RECORDER") && e.getReporterUsername().equals(guard.username())))
            throw new ApiException(403, "You cannot view a non-approved incident");
        guard.requireCanReadWard(e.getWard());
        return MiningResponse.from(e);
    }

    public List<MiningResponse> list() {
        guard.requireHazardRead();
        List<MiningIncident> rows = switch (guard.role()) {
            case "MINING_RECORDER" -> repo.findByWard(guard.ward());
            case "MINING_SUPERVISOR", "PROVINCIAL_ADMIN" -> repo.findAll();
            case "NATIONAL_USER" -> repo.findByStatus(IncidentStatus.APPROVED);
            default -> throw new ApiException(403, "Not authorized");
        };
        return rows.stream().map(MiningResponse::from).toList();
    }

    @Transactional
    public MiningResponse update(UUID id, MiningRequest r) {
        guard.requireNotNationalForWrite();
        MiningIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireRecorderOwnsWard(e.getWard());
        if (!e.getReporterUsername().equals(guard.username()))
            throw new ApiException(403, "You can only edit your own incidents");
        if (e.getStatus() == IncidentStatus.APPROVED || e.getStatus() == IncidentStatus.REJECTED)
            throw new ApiException(409, "Cannot modify an incident in status " + e.getStatus());
        e.setWard(r.ward()); e.setDistrict(r.district());
        e.setOccurredAt(r.occurredAt()); e.setSeverity(r.severity());
        e.setLatitude(r.latitude()); e.setLongitude(r.longitude());
        e.setMineName(r.mineName());
        e.setMineType(r.mineType());
        e.setAccidentType(r.accidentType());
        e.setTrappedMiners(r.trappedMiners());
        e.setInjuredMiners(r.injuredMiners());
        e.setFatalities(r.fatalities());
        e.setRescueOngoing(r.rescueOngoing());
        if (e.getStatus() == IncidentStatus.CORRECTION_REQUIRED) {
            audit(e.getId(), IncidentStatus.CORRECTION_REQUIRED, IncidentStatus.PENDING, "Resubmitted");
            e.setStatus(IncidentStatus.PENDING);
        }
        return MiningResponse.from(repo.save(e));
    }

    @Transactional
    public void delete(UUID id) {
        guard.requireNotNationalForWrite();
        MiningIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireRecorderOwnsWard(e.getWard());
        if (!e.getReporterUsername().equals(guard.username()))
            throw new ApiException(403, "You can only delete your own incidents");
        if (e.getStatus() == IncidentStatus.APPROVED)
            throw new ApiException(409, "Approved incidents cannot be deleted");
        repo.delete(e);
    }

    @Transactional
    public MiningResponse approve(UUID id) {
        guard.requireSupervisor();
        MiningIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.APPROVED, "Approved");
        e.setStatus(IncidentStatus.APPROVED);
        e = repo.save(e);
        publish(e);
        return MiningResponse.from(e);
    }

    @Transactional
    public MiningResponse reject(UUID id, String reason) {
        guard.requireSupervisor();
        MiningIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.REJECTED, reason);
        e.setStatus(IncidentStatus.REJECTED);
        e.setRejectionReason(reason);
        return MiningResponse.from(repo.save(e));
    }

    @Transactional
    public MiningResponse requestCorrection(UUID id, String reason) {
        guard.requireSupervisor();
        MiningIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.CORRECTION_REQUIRED, reason);
        e.setStatus(IncidentStatus.CORRECTION_REQUIRED);
        e.setRejectionReason(reason);
        return MiningResponse.from(repo.save(e));
    }

    public List<IncidentAudit> audits(UUID id) {
        guard.requireHazardRead();
        return auditRepo.findByIncidentIdOrderByTimestampAsc(id);
    }

    private MiningIncident loadPending(UUID id) {
        MiningIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
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

    private void publish(MiningIncident e) {
        try {
            boolean casualties = (e.getFatalities() != null && e.getFatalities() > 0)
                || (e.getInjuredMiners() != null && e.getInjuredMiners() > 0);
            rabbit.convertAndSend("dpdms.incidents", "incident.approved",
                new IncidentApprovedEvent(e.getId(), "MINING_ACCIDENT", e.getWard(), e.getDistrict(),
                    e.getSeverity().name(), e.getLatitude(), e.getLongitude(),
                    false, false, casualties));
        } catch (Exception ignored) {}
    }
}