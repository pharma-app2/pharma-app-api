package org.pharma.app.pharmaappapi.security.services;

import org.modelmapper.ModelMapper;
import org.pharma.app.pharmaappapi.exceptions.ResourceAlreadyExistsException;
import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;
import org.pharma.app.pharmaappapi.security.models.Role;
import org.pharma.app.pharmaappapi.security.models.RoleName;
import org.pharma.app.pharmaappapi.security.models.User;
import org.pharma.app.pharmaappapi.security.repositories.AuthRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {
    private final ModelMapper modelMapper;
    private final AuthRepository authRepository;

    public AuthServiceImpl(ModelMapper modelMapper, AuthRepository authRepository) {
        this.modelMapper = modelMapper;
        this.authRepository = authRepository;
    }

    @Override
    public void signUpPatient(SignUpPatientDTO signUpDTO) {
        Boolean patientExists = authRepository.existsByEmailAndRoleName(signUpDTO.getEmail(), RoleName.ROLE_PATIENT);

        if (patientExists) {
            throw new ResourceAlreadyExistsException("Patient", "email", signUpDTO.getEmail());
        }

        if (!signUpDTO.getPassword().equals(signUpDTO.getPasswordConfirmation())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Password and password confirmation don't match.");
        }

        User user = modelMapper.map(signUpDTO, User.class);
        Role role = new Role();

        role.setName(RoleName.ROLE_PATIENT);
        user.setRole(role);

        authRepository.save(user);
    }
}
