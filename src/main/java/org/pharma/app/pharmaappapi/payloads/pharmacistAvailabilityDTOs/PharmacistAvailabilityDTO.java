package org.pharma.app.pharmaappapi.payloads.pharmacistAvailabilityDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.pharma.app.pharmaappapi.validations.allowedDurations.AllowedDurations;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacistAvailabilityDTO {
    @NotNull
    @ToString.Include
    private OffsetDateTime startTime;

    @NotNull
    @AllowedDurations
    private Integer durationMinutes;
}
