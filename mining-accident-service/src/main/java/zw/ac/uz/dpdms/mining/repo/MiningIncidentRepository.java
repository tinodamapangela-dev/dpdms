package zw.ac.uz.dpdms.mining.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.ac.uz.dpdms.mining.common.IncidentStatus;
import zw.ac.uz.dpdms.mining.domain.MiningIncident;

import java.util.List;
import java.util.UUID;

public interface MiningIncidentRepository extends JpaRepository<MiningIncident, UUID> {
    List<MiningIncident> findByStatus(IncidentStatus status);
    List<MiningIncident> findByWard(String ward);
    List<MiningIncident> findByWardAndStatus(String ward, IncidentStatus status);
}