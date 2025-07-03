package org.pharma.app.pharmaappapi.security.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SignUpPatientDTO {
    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 50,
            message = "Field full_name must have between 3 and 50 characters"
    )
    private String full_name;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 100,
            message = "Field email must have between 3 and 100 characters"
    )
    private String email;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 255,
            message = "Field email must have between 3 and 255 characters"
    )
    private String password;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 255,
            message = "Field email must have between 3 and 255 characters"
    )
    private String passwordConfirmation;
}
