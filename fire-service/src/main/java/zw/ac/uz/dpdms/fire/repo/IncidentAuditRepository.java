package zw.ac.uz.dpdms.fire.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.ac.uz.dpdms.fire.common.IncidentAudit;

import java.util.List;
import java.util.UUID;

public interface IncidentAuditRepository extends JpaRepository<IncidentAudit, UUID> {
    List<IncidentAudit> findByIncidentIdOrderByTimestampAsc(UUID incidentId);
}