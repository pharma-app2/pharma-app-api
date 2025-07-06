package org.pharma.app.pharmaappapi.security.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pharma.app.pharmaappapi.exceptions.ConflictException;
import org.pharma.app.pharmaappapi.exceptions.ResourceAlreadyExistsException;
import org.pharma.app.pharmaappapi.exceptions.UnprocessableEntityException;
import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;
import org.pharma.app.pharmaappapi.security.models.Role;
import org.pharma.app.pharmaappapi.security.models.RoleName;
import org.pharma.app.pharmaappapi.security.models.User;
import org.pharma.app.pharmaappapi.security.repositories.AuthRepository;
import org.pharma.app.pharmaappapi.security.repositories.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthRepository authRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final String validFullName = "John Doe";
    private static final String invalidFullName = "";
    private static final String validPassword = "123abc";
    private static final String invalidPassword = "";
    private static final String validPasswordConfirmation = "123abc";
    private static final String invalidPasswordConfirmation = "";
    private static final String wrongPasswordConfirmation = "123abcd";
    private static final String validEmail = "john@doe.com";
    private static final String invalidEmail = "john_doe.com";
    private static final String validCpf = "12345678999";
    private static final String invalidCpf = "123456789999";

    @Test
    void signUpPatientSuccess() {
        SignUpPatientDTO signUpDTO = new SignUpPatientDTO(
                validFullName, validEmail, validPassword, validPasswordConfirmation, validCpf);

        // patientExists is set to false without querying db
        when(authRepository.existsByEmailAndRole_Name(signUpDTO.getEmail(), RoleName.ROLE_PATIENT)).thenReturn(false);
        when(authRepository.existsByPatient_CpfAndRole_Name(signUpDTO.getCpf(), RoleName.ROLE_PATIENT)).thenReturn(false);

        String hashedPassword = "hashedPassword123";
        when(passwordEncoder.encode(validPassword)).thenReturn(hashedPassword);

        Role patientRole = new Role(RoleName.ROLE_PATIENT);
        when(roleRepository.findFirstByName(RoleName.ROLE_PATIENT)).thenReturn(patientRole);

        authService.signUpPatient(signUpDTO);

        // Creates a new captor for record User type objects
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        // Verify if save method was called and a User object (which we can capture) was passed to it
        verify(authRepository).save(userArgumentCaptor.capture());
        // Get the User object we captured before
        User savedUser = userArgumentCaptor.getValue();

        assertThat(savedUser.getFullName()).isEqualTo(validFullName);
        assertThat(savedUser.getEmail()).isEqualTo(validEmail);
        assertThat(savedUser.getPassword()).isEqualTo(hashedPassword);
        assertThat(savedUser.getRole().getName()).isEqualTo(RoleName.ROLE_PATIENT);
        assertThat(savedUser.getPatient()).isNotNull();
        assertThat(savedUser.getPatient().getCpf()).isEqualTo(validCpf);
        assertThat(savedUser.getPatient().getUser()).isEqualTo(savedUser); // assert bidirectional relation
    }

    @Test
    void signUpPatientAlreadyExistsByEmailFail() {
        SignUpPatientDTO signUpDTO = new SignUpPatientDTO(validFullName, validEmail, validPassword, validPasswordConfirmation, validCpf);

        // patientExists is set to true without querying db
        when(authRepository.existsByEmailAndRole_Name(signUpDTO.getEmail(), RoleName.ROLE_PATIENT)).thenReturn(true);
        when(authRepository.existsByPatient_CpfAndRole_Name(signUpDTO.getCpf(), RoleName.ROLE_PATIENT)).thenReturn(false);

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.signUpPatient(signUpDTO));

        // Verify if save method was never called (because exception was thrown before it)
        verify(authRepository, never()).save(any(User.class));
    }

    @Test
    void signUpPatientAlreadyExistsByCpfFail() {
        SignUpPatientDTO signUpDTO = new SignUpPatientDTO(validFullName, validEmail, validPassword, validPasswordConfirmation, validCpf);

        // patientExists is set to true without querying db
        when(authRepository.existsByEmailAndRole_Name(signUpDTO.getEmail(), RoleName.ROLE_PATIENT)).thenReturn(false);
        when(authRepository.existsByPatient_CpfAndRole_Name(signUpDTO.getCpf(), RoleName.ROLE_PATIENT)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.signUpPatient(signUpDTO));

        // Verify if save method was never called (because exception was thrown before it)
        verify(authRepository, never()).save(any(User.class));
    }

    @Test
    void signUpPatientWrongConfirmationPasswordFail() {
        SignUpPatientDTO signUpDTO = new SignUpPatientDTO(validFullName, validEmail, validPassword, wrongPasswordConfirmation, validCpf);

        // patientExists is set to false without querying db
        when(authRepository.existsByPatient_CpfAndRole_Name(signUpDTO.getCpf(), RoleName.ROLE_PATIENT)).thenReturn(false);
        when(authRepository.existsByEmailAndRole_Name(signUpDTO.getEmail(), RoleName.ROLE_PATIENT)).thenReturn(false);

        assertThrows(UnprocessableEntityException.class, () -> authService.signUpPatient(signUpDTO));

        // Verify if save method was never called (because exception was thrown before it)
        verify(authRepository, never()).save(any(User.class));
    }
}