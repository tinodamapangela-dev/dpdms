package zw.ac.uz.dpdms.mining.dto;

import zw.ac.uz.dpdms.mining.common.IncidentStatus;
import zw.ac.uz.dpdms.mining.common.Severity;
import zw.ac.uz.dpdms.mining.domain.AccidentType;
import zw.ac.uz.dpdms.mining.domain.MineType;
import zw.ac.uz.dpdms.mining.domain.MiningIncident;

import java.time.LocalDateTime;
import java.util.UUID;

public record MiningResponse(
    UUID id, String ward, String district, String province,
    LocalDateTime occurredAt, String reporterUsername,
    Severity severity, IncidentStatus status,
    Double latitude, Double longitude,
    LocalDateTime createdAt, LocalDateTime updatedAt, String rejectionReason,
    String mineName, MineType mineType, AccidentType accidentType,
    Integer trappedMiners, Integer injuredMiners, Integer fatalities, Boolean rescueOngoing
) {
    public static MiningResponse from(MiningIncident i) {
        return new MiningResponse(
            i.getId(), i.getWard(), i.getDistrict(), i.getProvince(),
            i.getOccurredAt(), i.getReporterUsername(), i.getSeverity(), i.getStatus(),
            i.getLatitude(), i.getLongitude(), i.getCreatedAt(), i.getUpdatedAt(),
            i.getRejectionReason(),
            i.getMineName(), i.getMineType(), i.getAccidentType(),
            i.getTrappedMiners(), i.getInjuredMiners(), i.getFatalities(), i.getRescueOngoing());
    }
}