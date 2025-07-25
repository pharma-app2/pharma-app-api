package org.pharma.app.pharmaappapi.payloads.appointmentDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentDTO {
    @NotNull
    private UUID patientId;
    private UUID availabilityId;
    private Boolean isRemote;
}
