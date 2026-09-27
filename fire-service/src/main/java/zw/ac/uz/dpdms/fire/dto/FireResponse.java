package zw.ac.uz.dpdms.fire.dto;

import zw.ac.uz.dpdms.fire.common.IncidentStatus;
import zw.ac.uz.dpdms.fire.common.Severity;
import zw.ac.uz.dpdms.fire.domain.FireIncident;
import zw.ac.uz.dpdms.fire.domain.SuspectedCause;

import java.time.LocalDateTime;
import java.util.UUID;

public record FireResponse(
    UUID id, String ward, String district, String province,
    LocalDateTime occurredAt, String reporterUsername,
    Severity severity, IncidentStatus status,
    Double latitude, Double longitude,
    LocalDateTime createdAt, LocalDateTime updatedAt, String rejectionReason,
    Double areaBurnedHa, SuspectedCause suspectedCause,
    Integer injuries, Integer fatalities, Integer structuresDestroyed, Boolean active
) {
    public static FireResponse from(FireIncident i) {
        return new FireResponse(
            i.getId(), i.getWard(), i.getDistrict(), i.getProvince(),
            i.getOccurredAt(), i.getReporterUsername(), i.getSeverity(), i.getStatus(),
            i.getLatitude(), i.getLongitude(), i.getCreatedAt(), i.getUpdatedAt(),
            i.getRejectionReason(),
            i.getAreaBurnedHa(), i.getSuspectedCause(),
            i.getInjuries(), i.getFatalities(), i.getStructuresDestroyed(), i.getActive());
    }
}