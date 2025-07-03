package org.pharma.app.pharmaappapi.security.services;

import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;

public interface AuthService {
    void signUpPatient(SignUpPatientDTO signUpDTO);
}
