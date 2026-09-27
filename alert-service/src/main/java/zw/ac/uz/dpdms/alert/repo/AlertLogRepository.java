package zw.ac.uz.dpdms.alert.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.ac.uz.dpdms.alert.domain.AlertLog;
import java.util.UUID;

public interface AlertLogRepository extends JpaRepository<AlertLog, UUID> {}