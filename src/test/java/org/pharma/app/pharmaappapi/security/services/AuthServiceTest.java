package org.pharma.app.pharmaappapi.security.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.pharma.app.pharmaappapi.exceptions.ResourceAlreadyExistsException;
import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;
import org.pharma.app.pharmaappapi.security.models.RoleName;
import org.pharma.app.pharmaappapi.security.models.User;
import org.pharma.app.pharmaappapi.security.repositories.AuthRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthRepository authRepository;

    @Mock
    private ModelMapper modelMapper;

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

    @Test
    void signUpPatientSuccess() {
        SignUpPatientDTO signUpDTO = new SignUpPatientDTO(
                validFullName, validEmail, validPassword, validPasswordConfirmation);
        User mappedUser = new User();
        
        // patientExists is set to false without querying db
        when(authRepository.existsByEmailAndRoleName(signUpDTO.getEmail(), RoleName.ROLE_PATIENT))
                .thenReturn(false);
        // we don't need to test modelMapper. Just returns a mocked user (mappedUser)
        when(modelMapper.map(signUpDTO, User.class))
                .thenReturn(mappedUser);

        authService.signUpPatient(signUpDTO);

        // Creates a new captor for record User type objects
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        // Verify if save method was called and a User object (which we can capture) was passed to it
        verify(authRepository).save(userArgumentCaptor.capture());
        // Get the User object we captured before
        User savedUser = userArgumentCaptor.getValue();

        assertThat(savedUser.getRole().getName()).isEqualTo(RoleName.ROLE_PATIENT);
    }

    @Test
    void signUpPatientAlreadyExistsFail() {
        SignUpPatientDTO signUpDTO = new SignUpPatientDTO(validFullName, validEmail, validPassword, validPasswordConfirmation);

        // patientExists is set to true without querying db
        when(authRepository.existsByEmailAndRoleName(signUpDTO.getEmail(), RoleName.ROLE_PATIENT))
                .thenReturn(true);
        
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            authService.signUpPatient(signUpDTO);
        });

        // Verify if save method was never called (because exception was thrown before it)
        verify(authRepository, never()).save(any(User.class));
    }

    @Test
    void signUpPatientWrongConfirmationPasswordFail() {
        SignUpPatientDTO signUpDTO = new SignUpPatientDTO(validFullName, validEmail, validPassword, wrongPasswordConfirmation);

        // patientExists is set to false without querying db
        when(authRepository.existsByEmailAndRoleName(signUpDTO.getEmail(), RoleName.ROLE_PATIENT))
                .thenReturn(false);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authService.signUpPatient(signUpDTO);
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // Verify if save method was never called (because exception was thrown before it)
        verify(authRepository, never()).save(any(User.class));
    }
}