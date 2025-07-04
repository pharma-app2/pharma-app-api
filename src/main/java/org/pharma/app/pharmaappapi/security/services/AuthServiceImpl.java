package org.pharma.app.pharmaappapi.security.services;

import org.pharma.app.pharmaappapi.exceptions.ConflictAPIException;
import org.pharma.app.pharmaappapi.exceptions.ResourceAlreadyExistsException;
import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;
import org.pharma.app.pharmaappapi.security.models.Patient;
import org.pharma.app.pharmaappapi.security.models.Role;
import org.pharma.app.pharmaappapi.security.models.RoleName;
import org.pharma.app.pharmaappapi.security.models.User;
import org.pharma.app.pharmaappapi.security.repositories.AuthRepository;
import org.pharma.app.pharmaappapi.security.repositories.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, AuthRepository authRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void signUpPatient(SignUpPatientDTO signUpDTO) {
        String fullName = signUpDTO.getFullName();
        String email = signUpDTO.getEmail();
        String password = signUpDTO.getPassword();
        String passwordConfirmation = signUpDTO.getPasswordConfirmation();
        String cpf = signUpDTO.getCpf();

        Boolean patientExistsByEmail = authRepository.existsByEmailAndRole_Name(email, RoleName.ROLE_PATIENT);
        Boolean patientExistsByCpf = authRepository.existsByPatient_CpfAndRole_Name(cpf, RoleName.ROLE_PATIENT);

        if (patientExistsByCpf) {
            throw new ResourceAlreadyExistsException("Patient", "cpf", cpf);
        }
        if (patientExistsByEmail) {
            throw new ResourceAlreadyExistsException("Patient", "email", email);
        }

        if (!password.equals(passwordConfirmation)) {
            throw new ConflictAPIException("Password and password confirmation don't match.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(fullName, email, encodedPassword);

        Patient patient = new Patient(cpf);
        user.setPatient(patient);
        patient.setUser(user);

        Role role = roleRepository.findFirstByName(RoleName.ROLE_PATIENT);
        user.setRole(role);

        authRepository.save(user);
    }
}
