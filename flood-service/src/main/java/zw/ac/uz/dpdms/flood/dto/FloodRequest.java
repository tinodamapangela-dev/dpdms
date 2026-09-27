package zw.ac.uz.dpdms.flood.dto;

import jakarta.validation.constraints.*;
import zw.ac.uz.dpdms.flood.common.Severity;

import java.time.LocalDateTime;

public record FloodRequest(
    @NotBlank String ward,
    @NotBlank String district,
    @NotNull LocalDateTime occurredAt,
    @NotNull Severity severity,
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
    @NotNull @Positive Double peakWaterLevelM,
    @NotBlank String riverBasin,
    @NotNull @PositiveOrZero Integer householdsDisplaced,
    @NotNull @PositiveOrZero Double areaFloodedHectares,
    @NotNull @PositiveOrZero Integer inundationDays
) {}