package org.pharma.app.pharmaappapi.security.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;
import org.pharma.app.pharmaappapi.security.config.SecurityConfig;
import org.pharma.app.pharmaappapi.security.exceptions.CustomAuthEntryPoint;
import org.pharma.app.pharmaappapi.security.jwt.JwtUtils;
import org.pharma.app.pharmaappapi.security.services.AuthService;
import org.pharma.app.pharmaappapi.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // It's a mock, not the real authService. We have to add it because mocks doesn't add beans
    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private CustomAuthEntryPoint customAuthEntryPoint;

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
    void signUpPatientSuccess() throws Exception {
        SignUpPatientDTO signUpDTO = new SignUpPatientDTO(
                validFullName, validEmail, validPassword, validPasswordConfirmation, validCpf);

        // Verify if method returns nothing (void)
        doNothing().when(authService).signUpPatient(any(SignUpPatientDTO.class));

        mockMvc.perform(post("/api/auth/signup/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDTO))) // Convert DTO to JSON

                .andExpect(status().isCreated());

        // Verify if method was called exactly once
        verify(authService).signUpPatient(any(SignUpPatientDTO.class));
    }

    @Test
    void signUpPatientInvalidDataFail() throws Exception {
        SignUpPatientDTO invalidSignUpDTO = new SignUpPatientDTO(
                invalidFullName, validEmail, validPassword, validPasswordConfirmation, validCpf);

        mockMvc.perform(post("/api/auth/signup/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSignUpDTO)))

                .andExpect(status().isUnprocessableEntity());

        // Verify if service method was never called (because validation failed before it)
        verify(authService, never()).signUpPatient(any());
    }
}