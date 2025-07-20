package org.pharma.app.pharmaappapi.payloads.patientDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientSearchByNameDTO {
    @NotNull
    private UUID id;

    @NotNull
    private String fullName;

    @NotNull
    private String email;
}
