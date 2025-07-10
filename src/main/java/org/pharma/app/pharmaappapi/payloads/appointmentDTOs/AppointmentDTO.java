package org.pharma.app.pharmaappapi.payloads.appointmentDTOs;

import jakarta.validation.constraints.NotNull;
import org.pharma.app.pharmaappapi.validations.allowedDurations.AllowedDurations;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentDTO(
        @NotNull UUID patientId,
        @NotNull UUID pharmacistId,
        @NotNull OffsetDateTime scheduledAt,
        @NotNull @AllowedDurations Integer durationMinutes,
        @NotNull UUID modalityId,
        @NotNull UUID statusId,
        String patientReason,
        String pharmacistNotes
) {}
