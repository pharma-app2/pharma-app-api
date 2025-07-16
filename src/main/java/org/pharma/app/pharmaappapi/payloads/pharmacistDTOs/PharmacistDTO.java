package org.pharma.app.pharmaappapi.payloads.pharmacistDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pharma.app.pharmaappapi.payloads.pharmacistLocationDTOs.PharmacistLocationDTO;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacistDTO {
    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 20,
            message = "Field crf must have between 3 and 20 characters"
    )
    private String crf;

    @NotNull
    private Boolean acceptsRemote;

    @NotNull
    private Set<UUID> healthPlanIds;

    @NotNull
    private Set<PharmacistLocationDTO> pharmacistLocations;
}
