package org.pharma.app.pharmaappapi.payloads.appointmentDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentStatusName;
import org.pharma.app.pharmaappapi.validations.allowedDurations.AllowedDurations;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class AppointmentDTO {
    private @NotNull String id;
    private @NotNull AppointmentPatientDTO patient;
    private @NotNull LocalDateTime startTime;
    private @NotNull @AllowedDurations Integer durationMinutes;
    private @NotNull EventType type;
    private @NotNull AppointmentStatusName status;
//    private @NotNull UUID availabilityId;
//    private @NotNull UUID statusId;
//    private @NotNull Boolean isRemote;
//    private String patientReason;
//    private String pharmacistNotes;
}