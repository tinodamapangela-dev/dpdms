package zw.ac.uz.dpdms.flood.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.ac.uz.dpdms.flood.common.IncidentStatus;
import zw.ac.uz.dpdms.flood.domain.FloodIncident;

import java.util.List;
import java.util.UUID;

public interface FloodIncidentRepository extends JpaRepository<FloodIncident, UUID> {
    List<FloodIncident> findByStatus(IncidentStatus status);
    List<FloodIncident> findByWard(String ward);
    List<FloodIncident> findByWardAndStatus(String ward, IncidentStatus status);
}