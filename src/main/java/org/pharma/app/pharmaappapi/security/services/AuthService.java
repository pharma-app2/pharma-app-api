package org.pharma.app.pharmaappapi.security.services;

import org.pharma.app.pharmaappapi.security.DTOs.LoginResponse;
import org.pharma.app.pharmaappapi.security.DTOs.SignInPatientDTO;
import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;
import org.pharma.app.pharmaappapi.security.DTOs.UserInfoDTO;
import org.springframework.http.ResponseCookie;

import java.util.UUID;

public interface AuthService {
    void signUpPatient(SignUpPatientDTO signUpDTO);
    LoginResponse signInPatient(SignInPatientDTO signInDTO);
    UserInfoDTO getCurrentUserInfoByUserDetails(UserDetailsImpl userDetails);
    ResponseCookie getCleanJwtCookie();
}
