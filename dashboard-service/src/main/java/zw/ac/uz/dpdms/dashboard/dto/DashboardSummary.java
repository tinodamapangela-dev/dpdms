package zw.ac.uz.dpdms.dashboard.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record DashboardSummary(
    long totalApproved,
    Map<String, Long> byHazard,
    Map<String, Long> bySeverity,
    Map<String, Long> byStatus,
    Map<LocalDate, Long> overTime,
    List<IncidentView> recent,
    List<IncidentView> incidents
) {}