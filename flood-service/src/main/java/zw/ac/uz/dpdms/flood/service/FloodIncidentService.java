package zw.ac.uz.dpdms.flood.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.ac.uz.dpdms.flood.common.*;
import zw.ac.uz.dpdms.flood.domain.FloodIncident;
import zw.ac.uz.dpdms.flood.dto.FloodRequest;
import zw.ac.uz.dpdms.flood.dto.FloodResponse;
import zw.ac.uz.dpdms.flood.repo.FloodIncidentRepository;
import zw.ac.uz.dpdms.flood.repo.IncidentAuditRepository;
import zw.ac.uz.dpdms.flood.security.ScopeGuard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FloodIncidentService {

    private final FloodIncidentRepository repo;
    private final IncidentAuditRepository auditRepo;
    private final ScopeGuard guard;
    private final RabbitTemplate rabbit;

    public FloodIncidentService(FloodIncidentRepository repo, IncidentAuditRepository auditRepo,
                                ScopeGuard guard, RabbitTemplate rabbit) {
        this.repo = repo;
        this.auditRepo = auditRepo;
        this.guard = guard;
        this.rabbit = rabbit;
    }

    @Transactional
    public FloodResponse create(FloodRequest r) {
        guard.requireNotNationalForWrite();
        guard.requireRecorderOwnsWard(r.ward());

        FloodIncident e = new FloodIncident();
        e.setWard(r.ward());
        e.setDistrict(r.district());
        e.setProvince("Rushinga");
        e.setOccurredAt(r.occurredAt());
        e.setSeverity(r.severity());
        e.setLatitude(r.latitude());
        e.setLongitude(r.longitude());
        e.setReporterUsername(guard.username());
        e.setStatus(IncidentStatus.PENDING);
        e.setPeakWaterLevelM(r.peakWaterLevelM());
        e.setRiverBasin(r.riverBasin());
        e.setHouseholdsDisplaced(r.householdsDisplaced());
        e.setAreaFloodedHectares(r.areaFloodedHectares());
        e.setInundationDays(r.inundationDays());
        e = repo.save(e);
        audit(e.getId(), null, IncidentStatus.PENDING, "Created by recorder");
        return FloodResponse.from(e);
    }

    public FloodResponse get(UUID id) {
        FloodIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Flood incident not found"));
        guard.requireHazardRead();
        if (e.getStatus() != IncidentStatus.APPROVED
            && !guard.role().equals("FLOOD_SUPERVISOR")
            && !guard.role().equals("PROVINCIAL_ADMIN")
            && !(guard.role().equals("FLOOD_RECORDER") && e.getReporterUsername().equals(guard.username())))
            throw new ApiException(403, "You cannot view a non-approved incident");
        guard.requireCanReadWard(e.getWard());
        return FloodResponse.from(e);
    }

    public List<FloodResponse> list() {
        guard.requireHazardRead();
        List<FloodIncident> rows = switch (guard.role()) {
            case "FLOOD_RECORDER" -> repo.findByWard(guard.ward());
            case "FLOOD_SUPERVISOR", "PROVINCIAL_ADMIN" -> repo.findAll();
            case "NATIONAL_USER" -> repo.findByStatus(IncidentStatus.APPROVED);
            default -> throw new ApiException(403, "Not authorized");
        };
        return rows.stream().map(FloodResponse::from).toList();
    }

    @Transactional
    public FloodResponse update(UUID id, FloodRequest r) {
        guard.requireNotNationalForWrite();
        FloodIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireRecorderOwnsWard(e.getWard());
        if (!e.getReporterUsername().equals(guard.username()))
            throw new ApiException(403, "You can only edit your own incidents");
        if (e.getStatus() == IncidentStatus.APPROVED || e.getStatus() == IncidentStatus.REJECTED)
            throw new ApiException(409, "Cannot modify an incident in status " + e.getStatus());
        e.setWard(r.ward());
        e.setDistrict(r.district());
        e.setOccurredAt(r.occurredAt());
        e.setSeverity(r.severity());
        e.setLatitude(r.latitude());
        e.setLongitude(r.longitude());
        e.setPeakWaterLevelM(r.peakWaterLevelM());
        e.setRiverBasin(r.riverBasin());
        e.setHouseholdsDisplaced(r.householdsDisplaced());
        e.setAreaFloodedHectares(r.areaFloodedHectares());
        e.setInundationDays(r.inundationDays());
        if (e.getStatus() == IncidentStatus.CORRECTION_REQUIRED) {
            audit(e.getId(), IncidentStatus.CORRECTION_REQUIRED, IncidentStatus.PENDING, "Resubmitted after correction");
            e.setStatus(IncidentStatus.PENDING);
        }
        return FloodResponse.from(repo.save(e));
    }

    @Transactional
    public void delete(UUID id) {
        guard.requireNotNationalForWrite();
        FloodIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
        guard.requireRecorderOwnsWard(e.getWard());
        if (!e.getReporterUsername().equals(guard.username()))
            throw new ApiException(403, "You can only delete your own incidents");
        if (e.getStatus() == IncidentStatus.APPROVED)
            throw new ApiException(409, "Approved incidents cannot be deleted");
        repo.delete(e);
    }

    @Transactional
    public FloodResponse approve(UUID id) {
        guard.requireSupervisor();
        FloodIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.APPROVED, "Approved");
        e.setStatus(IncidentStatus.APPROVED);
        e = repo.save(e);
        publishApproved(e);
        return FloodResponse.from(e);
    }

    @Transactional
    public FloodResponse reject(UUID id, String reason) {
        guard.requireSupervisor();
        FloodIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.REJECTED, reason);
        e.setStatus(IncidentStatus.REJECTED);
        e.setRejectionReason(reason);
        return FloodResponse.from(repo.save(e));
    }

    @Transactional
    public FloodResponse requestCorrection(UUID id, String reason) {
        guard.requireSupervisor();
        FloodIncident e = loadPending(id);
        audit(e.getId(), e.getStatus(), IncidentStatus.CORRECTION_REQUIRED, reason);
        e.setStatus(IncidentStatus.CORRECTION_REQUIRED);
        e.setRejectionReason(reason);
        return FloodResponse.from(repo.save(e));
    }

    public List<IncidentAudit> audits(UUID id) {
        guard.requireHazardRead();
        return auditRepo.findByIncidentIdOrderByTimestampAsc(id);
    }

    private FloodIncident loadPending(UUID id) {
        FloodIncident e = repo.findById(id).orElseThrow(() -> new ApiException(404, "Not found"));
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

    private void publishApproved(FloodIncident e) {
        try {
            rabbit.convertAndSend("dpdms.incidents", "incident.approved",
                new IncidentApprovedEvent(e.getId(), "FLOOD", e.getWard(), e.getDistrict(),
                    e.getSeverity().name(), e.getLatitude(), e.getLongitude(),
                    e.getPeakWaterLevelM() != null && e.getPeakWaterLevelM() > 5.0,
                    false, false));
        } catch (Exception ignored) {
            // alerts must never block incident capture
        }
    }
}