package org.pharma.app.pharmaappapi.security.services;

import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;
import org.pharma.app.pharmaappapi.security.models.RoleName;
import org.pharma.app.pharmaappapi.security.repositories.AuthRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepository;

    public AuthServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public void signUpPatient(SignUpPatientDTO signUpDTO) {
        Boolean patientExists = authRepository.existsByEmailAndRoleName(signUpDTO.getEmail(), RoleName.ROLE_PATIENT);

        System.out.println("Exists: " + patientExists);
    }
}
