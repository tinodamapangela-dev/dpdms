package zw.ac.uz.dpdms.zoonotic.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.ac.uz.dpdms.zoonotic.common.IncidentStatus;
import zw.ac.uz.dpdms.zoonotic.domain.ZoonoticIncident;

import java.util.List;
import java.util.UUID;

public interface ZoonoticIncidentRepository extends JpaRepository<ZoonoticIncident, UUID> {
    List<ZoonoticIncident> findByStatus(IncidentStatus status);
    List<ZoonoticIncident> findByWard(String ward);
    List<ZoonoticIncident> findByWardAndStatus(String ward, IncidentStatus status);
}