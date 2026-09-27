package zw.ac.uz.dpdms.report.dto;

import java.time.LocalDateTime;

public record ReportRow(
    String hazard, String id, String ward, String district,
    LocalDateTime occurredAt, String severity, String status,
    Double latitude, Double longitude, String details
) {}