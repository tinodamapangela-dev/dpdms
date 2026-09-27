package zw.ac.uz.dpdms.zoonotic.dto;

import zw.ac.uz.dpdms.zoonotic.common.IncidentStatus;
import zw.ac.uz.dpdms.zoonotic.common.Severity;
import zw.ac.uz.dpdms.zoonotic.domain.Classification;
import zw.ac.uz.dpdms.zoonotic.domain.ZoonoticIncident;

import java.time.LocalDateTime;
import java.util.UUID;

public record ZoonoticResponse(
    UUID id, String ward, String district, String province,
    LocalDateTime occurredAt, String reporterUsername,
    Severity severity, IncidentStatus status,
    Double latitude, Double longitude,
    LocalDateTime createdAt, LocalDateTime updatedAt, String rejectionReason,
    String pathogen, String animalSpecies,
    Integer confirmedHumanCases, Integer confirmedAnimalCases, Classification classification
) {
    public static ZoonoticResponse from(ZoonoticIncident i) {
        return new ZoonoticResponse(
            i.getId(), i.getWard(), i.getDistrict(), i.getProvince(),
            i.getOccurredAt(), i.getReporterUsername(), i.getSeverity(), i.getStatus(),
            i.getLatitude(), i.getLongitude(), i.getCreatedAt(), i.getUpdatedAt(),
            i.getRejectionReason(),
            i.getPathogen(), i.getAnimalSpecies(),
            i.getConfirmedHumanCases(), i.getConfirmedAnimalCases(), i.getClassification());
    }
}