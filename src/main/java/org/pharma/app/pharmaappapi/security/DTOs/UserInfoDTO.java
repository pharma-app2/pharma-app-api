package org.pharma.app.pharmaappapi.security.DTOs;

import java.time.LocalDate;

public sealed interface UserInfo permits PatientInfoDTO, PharmacistInfoDTO, UserInfoDTO {

}


record UserInfoDTO(
        String email,
        LocalDate fullName
) implements UserInfo {}

record PatientInfoDTO(
        String cpf,
        LocalDate birthday
) implements UserInfo {}

record PharmacistInfoDTO(
        String crf
) implements UserInfo {}