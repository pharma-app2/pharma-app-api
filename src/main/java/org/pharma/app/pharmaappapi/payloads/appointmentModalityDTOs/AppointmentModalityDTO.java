package org.pharma.app.pharmaappapi.payloads.appointmentModalityDTOs;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AppointmentModalityDTO(
        @NotNull UUID id,
        @NotNull String name
) {}
