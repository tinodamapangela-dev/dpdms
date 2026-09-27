package zw.ac.uz.dpdms.flood.dto;

import zw.ac.uz.dpdms.flood.common.IncidentStatus;
import zw.ac.uz.dpdms.flood.common.Severity;
import zw.ac.uz.dpdms.flood.domain.FloodIncident;

import java.time.LocalDateTime;
import java.util.UUID;

public record FloodResponse(
    UUID id, String ward, String district, String province,
    LocalDateTime occurredAt, String reporterUsername,
    Severity severity, IncidentStatus status,
    Double latitude, Double longitude,
    LocalDateTime createdAt, LocalDateTime updatedAt, String rejectionReason,
    Double peakWaterLevelM, String riverBasin,
    Integer householdsDisplaced, Double areaFloodedHectares, Integer inundationDays
) {
    public static FloodResponse from(FloodIncident i) {
        return new FloodResponse(
            i.getId(), i.getWard(), i.getDistrict(), i.getProvince(),
            i.getOccurredAt(), i.getReporterUsername(), i.getSeverity(), i.getStatus(),
            i.getLatitude(), i.getLongitude(), i.getCreatedAt(), i.getUpdatedAt(),
            i.getRejectionReason(),
            i.getPeakWaterLevelM(), i.getRiverBasin(),
            i.getHouseholdsDisplaced(), i.getAreaFloodedHectares(), i.getInundationDays());
    }
}