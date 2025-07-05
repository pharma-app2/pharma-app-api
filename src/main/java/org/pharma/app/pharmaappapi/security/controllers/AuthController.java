package org.pharma.app.pharmaappapi.security.controllers;

import jakarta.validation.Valid;
import org.pharma.app.pharmaappapi.security.DTOs.SignInPatientDTO;
import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;
import org.pharma.app.pharmaappapi.security.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup/patient")
    public ResponseEntity<?> signUpPatient(@RequestBody @Valid SignUpPatientDTO signUpDTO) {
        authService.signUpPatient(signUpDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/signin/patient")
    public ResponseEntity<String> signInPatient(@RequestBody @Valid SignInPatientDTO signInPatientDTO) {
        String message = authService.signInPatient(signInPatientDTO);

        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
