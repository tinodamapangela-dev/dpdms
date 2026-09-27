package zw.ac.uz.dpdms.drought.dto;

import zw.ac.uz.dpdms.drought.common.IncidentStatus;
import zw.ac.uz.dpdms.drought.common.Severity;
import zw.ac.uz.dpdms.drought.domain.DroughtIncident;

import java.time.LocalDateTime;
import java.util.UUID;

public record DroughtResponse(
    UUID id, String ward, String district, String province,
    LocalDateTime occurredAt, String reporterUsername,
    Severity severity, IncidentStatus status,
    Double latitude, Double longitude,
    LocalDateTime createdAt, LocalDateTime updatedAt, String rejectionReason,
    Double rainfallDeficitMm, Integer consecutiveDryDays,
    Double cropFailurePct, Integer peopleWaterShortage, Integer livestockMortality
) {
    public static DroughtResponse from(DroughtIncident i) {
        return new DroughtResponse(
            i.getId(), i.getWard(), i.getDistrict(), i.getProvince(),
            i.getOccurredAt(), i.getReporterUsername(), i.getSeverity(), i.getStatus(),
            i.getLatitude(), i.getLongitude(), i.getCreatedAt(), i.getUpdatedAt(),
            i.getRejectionReason(),
            i.getRainfallDeficitMm(), i.getConsecutiveDryDays(),
            i.getCropFailurePct(), i.getPeopleWaterShortage(), i.getLivestockMortality());
    }
}