package zw.ac.uz.dpdms.fire.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.ac.uz.dpdms.fire.common.IncidentStatus;
import zw.ac.uz.dpdms.fire.domain.FireIncident;

import java.util.List;
import java.util.UUID;

public interface FireIncidentRepository extends JpaRepository<FireIncident, UUID> {
    List<FireIncident> findByStatus(IncidentStatus status);
    List<FireIncident> findByWard(String ward);
    List<FireIncident> findByWardAndStatus(String ward, IncidentStatus status);
}