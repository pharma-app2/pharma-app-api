package org.pharma.app.pharmaappapi.payloads.appointmentDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentPatientDTO {
    private @NotNull UUID id;
    private @NotNull String name;
    private @NotNull @Email String email;
}
