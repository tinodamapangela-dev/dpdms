package zw.ac.uz.dpdms.fire.dto;

import jakarta.validation.constraints.*;
import zw.ac.uz.dpdms.fire.common.Severity;
import zw.ac.uz.dpdms.fire.domain.SuspectedCause;

import java.time.LocalDateTime;

public record FireRequest(
    @NotBlank String ward,
    @NotBlank String district,
    @NotNull LocalDateTime occurredAt,
    @NotNull Severity severity,
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
    @NotNull @PositiveOrZero Double areaBurnedHa,
    @NotNull SuspectedCause suspectedCause,
    @NotNull @PositiveOrZero Integer injuries,
    @NotNull @PositiveOrZero Integer fatalities,
    @NotNull @PositiveOrZero Integer structuresDestroyed,
    @NotNull Boolean active
) {}