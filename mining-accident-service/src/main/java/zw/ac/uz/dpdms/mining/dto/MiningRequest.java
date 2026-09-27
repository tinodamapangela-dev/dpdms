package zw.ac.uz.dpdms.mining.dto;

import jakarta.validation.constraints.*;
import zw.ac.uz.dpdms.mining.common.Severity;
import zw.ac.uz.dpdms.mining.domain.AccidentType;
import zw.ac.uz.dpdms.mining.domain.MineType;

import java.time.LocalDateTime;

public record MiningRequest(
    @NotBlank String ward,
    @NotBlank String district,
    @NotNull LocalDateTime occurredAt,
    @NotNull Severity severity,
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
    @NotBlank String mineName,
    @NotNull MineType mineType,
    @NotNull AccidentType accidentType,
    @NotNull @PositiveOrZero Integer trappedMiners,
    @NotNull @PositiveOrZero Integer injuredMiners,
    @NotNull @PositiveOrZero Integer fatalities,
    @NotNull Boolean rescueOngoing
) {}