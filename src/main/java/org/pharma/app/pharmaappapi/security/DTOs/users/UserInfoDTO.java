package org.pharma.app.pharmaappapi.security.DTOs.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@Setter
public class UserInfoDTO {
    private String email;
    private String fullName;
    private String cpf;
    private LocalDate birthday;
    private String crf;
}