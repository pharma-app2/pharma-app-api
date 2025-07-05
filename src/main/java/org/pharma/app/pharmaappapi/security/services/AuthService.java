package org.pharma.app.pharmaappapi.security.services;

import org.pharma.app.pharmaappapi.security.DTOs.LoginResponse;
import org.pharma.app.pharmaappapi.security.DTOs.SignInPatientDTO;
import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;

public interface AuthService {
    void signUpPatient(SignUpPatientDTO signUpDTO);
    LoginResponse signInPatient(SignInPatientDTO signInDTO);
}
