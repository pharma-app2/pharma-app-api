package org.pharma.app.pharmaappapi.security.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpPatientDTO {
    public SignUpPatientDTO(String fullName, String email, String password, String passwordConfirmation) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }

    private UUID id;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 50,
            message = "Field fullName must have between 3 and 50 characters"
    )
    private String fullName;

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
            message = "Field password must have between 3 and 255 characters"
    )
    private String password;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 255,
            message = "Field passwordConfirmation must have between 3 and 255 characters"
    )
    private String passwordConfirmation;
}
