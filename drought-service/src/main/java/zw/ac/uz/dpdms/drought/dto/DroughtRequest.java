package zw.ac.uz.dpdms.drought.dto;

import jakarta.validation.constraints.*;
import zw.ac.uz.dpdms.drought.common.Severity;

import java.time.LocalDateTime;

public record DroughtRequest(
    @NotBlank String ward,
    @NotBlank String district,
    @NotNull LocalDateTime occurredAt,
    @NotNull Severity severity,
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
    @NotNull @PositiveOrZero Double rainfallDeficitMm,
    @NotNull @PositiveOrZero Integer consecutiveDryDays,
    @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double cropFailurePct,
    @NotNull @PositiveOrZero Integer peopleWaterShortage,
    @NotNull @PositiveOrZero Integer livestockMortality
) {}