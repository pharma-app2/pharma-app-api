package org.pharma.app.pharmaappapi.payloads.appointmentDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.pharma.app.pharmaappapi.validations.allowedDurations.AllowedDurations;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class AppointmentDTO {
    private @NotNull UUID patientId;
    private @NotNull UUID pharmacistId;
    private @NotNull OffsetDateTime scheduledAt;
    private @NotNull @AllowedDurations Integer durationMinutes;
    private @NotNull UUID modalityId;
    private @NotNull UUID statusId;
    private String patientReason;
    private String pharmacistNotes;
}