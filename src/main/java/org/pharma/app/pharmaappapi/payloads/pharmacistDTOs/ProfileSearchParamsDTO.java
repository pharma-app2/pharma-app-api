package org.pharma.app.pharmaappapi.payloads.pharmacistDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSearchParamsDTO {
    @ToString.Include
    private String pharmacistName;

    @ToString.Include
    private String id;

    @NotNull
    @ToString.Include
    private String address;

    @NotNull
    @ToString.Include
    private String ibgeApiCity;

    @NotNull
    @ToString.Include
    private String ibgeApiState;

    @ToString.Include
    private String acceptsRemote;
}
