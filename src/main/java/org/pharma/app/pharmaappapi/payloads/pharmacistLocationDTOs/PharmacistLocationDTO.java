package org.pharma.app.pharmaappapi.payloads.pharmacistLocationDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacistLocationDTO {
    @NotNull
    @NotBlank
    @Size(
            min = 8,
            max = 15,
            message = "Phone number must have between 8 and 15 characters"
    )
    private String phone1;

    @Size(
            min = 8,
            max = 15,
            message = "Phone number must have between 8 and 15 characters"
    )
    private String phone2;

    @Size(
            min = 8,
            max = 15,
            message = "Phone number must have between 8 and 15 characters"
    )
    private String phone3;

    @NotNull
    private String address;

    // we can't add a suffix/prefix "id" due to intelligent mapping from modelMapper
    @NotNull
    private Integer ibgeApiIdentifierCity;
}
