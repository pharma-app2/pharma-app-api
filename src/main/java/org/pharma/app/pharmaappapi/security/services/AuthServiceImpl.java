package org.pharma.app.pharmaappapi.security.services;

import org.modelmapper.ModelMapper;
import org.pharma.app.pharmaappapi.exceptions.ResourceAlreadyExistsException;
import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;
import org.pharma.app.pharmaappapi.security.models.Patient;
import org.pharma.app.pharmaappapi.security.models.Role;
import org.pharma.app.pharmaappapi.security.models.RoleName;
import org.pharma.app.pharmaappapi.security.models.User;
import org.pharma.app.pharmaappapi.security.repositories.AuthRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, AuthRepository authRepository) {
        this.passwordEncoder = passwordEncoder;
        this.authRepository = authRepository;
    }

    @Override
    public void signUpPatient(SignUpPatientDTO signUpDTO) {
        String fullName = signUpDTO.getFullName();
        String email = signUpDTO.getEmail();
        String password = signUpDTO.getPassword();
        String passwordConfirmation = signUpDTO.getPasswordConfirmation();
        String cpf = signUpDTO.getCpf();

        Boolean patientExists = authRepository.existsByPatient_CpfOrEmailAndRole(cpf, email, RoleName.ROLE_PATIENT);

        if (patientExists) {
            throw new ResourceAlreadyExistsException("Patient", "email", email);
        }

        if (!password.equals(passwordConfirmation)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Password and password confirmation don't match.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(fullName, email, encodedPassword);

        Patient patient = new Patient(cpf);
        user.setPatient(patient);

        Role role = new Role();
        role.setName(RoleName.ROLE_PATIENT);
        user.setRole(role);

        authRepository.save(user);
    }
}
