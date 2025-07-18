package org.pharma.app.pharmaappapi.payloads.availabilityDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentStatusName;
import org.pharma.app.pharmaappapi.validations.allowedDurations.AllowedDurations;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnAvailabilityDTO {
    private UUID id;

    @NotNull
    private LocalDateTime startTime;

    @AllowedDurations
    private Integer durationMinutes;

    @NotNull
    private AppointmentOrAvailability type;

    @NotNull
    private String patientName;

    @NotNull
    private AppointmentStatusName status;
}
