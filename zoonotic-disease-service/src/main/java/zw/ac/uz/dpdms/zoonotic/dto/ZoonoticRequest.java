package zw.ac.uz.dpdms.zoonotic.dto;

import jakarta.validation.constraints.*;
import zw.ac.uz.dpdms.zoonotic.common.Severity;
import zw.ac.uz.dpdms.zoonotic.domain.Classification;

import java.time.LocalDateTime;

public record ZoonoticRequest(
    @NotBlank String ward,
    @NotBlank String district,
    @NotNull LocalDateTime occurredAt,
    @NotNull Severity severity,
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
    @NotBlank String pathogen,
    @NotBlank String animalSpecies,
    @NotNull @PositiveOrZero Integer confirmedHumanCases,
    @NotNull @PositiveOrZero Integer confirmedAnimalCases,
    @NotNull Classification classification
) {}