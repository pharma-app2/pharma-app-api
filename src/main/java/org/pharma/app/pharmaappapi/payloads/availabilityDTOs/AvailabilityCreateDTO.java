package org.pharma.app.pharmaappapi.payloads.availabilityDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.pharma.app.pharmaappapi.validations.allowedDurations.AllowedDurations;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityCreateDTO {
    private UUID id;

    @NotNull
    @ToString.Include
    private CustomLocalDateTime startLocalDateTime;

    @NotNull
    @AllowedDurations
    private Integer durationMinutes;
}
