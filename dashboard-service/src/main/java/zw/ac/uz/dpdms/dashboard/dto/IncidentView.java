package zw.ac.uz.dpdms.dashboard.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record IncidentView(
    UUID id, String hazard, String ward, String district,
    LocalDateTime occurredAt, String severity, String status,
    Double latitude, Double longitude
) {}