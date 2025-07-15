package org.pharma.app.pharmaappapi.payloads.appointmentDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.pharma.app.pharmaappapi.validations.allowedDurations.AllowedDurations;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class AppointmentDTO {
    private @NotNull UUID patientId;
    private @NotNull @AllowedDurations Integer durationMinutes;
    private @NotNull UUID modalityId;
    private @NotNull UUID availabilityId;
    private @NotNull UUID statusId;
    private String patientReason;
    private String pharmacistNotes;
}