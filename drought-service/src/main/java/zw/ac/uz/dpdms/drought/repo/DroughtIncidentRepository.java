package zw.ac.uz.dpdms.drought.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.ac.uz.dpdms.drought.common.IncidentStatus;
import zw.ac.uz.dpdms.drought.domain.DroughtIncident;

import java.util.List;
import java.util.UUID;

public interface DroughtIncidentRepository extends JpaRepository<DroughtIncident, UUID> {
    List<DroughtIncident> findByStatus(IncidentStatus status);
    List<DroughtIncident> findByWard(String ward);
    List<DroughtIncident> findByWardAndStatus(String ward, IncidentStatus status);
}