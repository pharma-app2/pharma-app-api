package org.pharma.app.pharmaappapi.security.controllers;

import jakarta.validation.Valid;
import org.pharma.app.pharmaappapi.security.DTOs.LoginResponse;
import org.pharma.app.pharmaappapi.security.DTOs.SignInPatientDTO;
import org.pharma.app.pharmaappapi.security.DTOs.SignUpPatientDTO;
import org.pharma.app.pharmaappapi.security.DTOs.UserInfoDTO;
import org.pharma.app.pharmaappapi.security.services.AuthService;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getCurrentUserInfo(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        UserInfoDTO currentUserInfo = authService.getCurrentUserInfoByUserDetails(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(currentUserInfo);
    }


    @PostMapping("/auth/signup/patient")
    public ResponseEntity<?> signUpPatient(@RequestBody @Valid SignUpPatientDTO signUpDTO) {
        authService.signUpPatient(signUpDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/auth/signin/patient")
    public ResponseEntity<LoginResponse> signInPatient(@RequestBody @Valid SignInPatientDTO signInPatientDTO) {
        LoginResponse response = authService.signInPatient(signInPatientDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, response.jwtCookie().toString())
                .body(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser() {
        ResponseCookie cleanJwtCookie = authService.getCleanJwtCookie();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cleanJwtCookie.toString())
                .body(null);
    }
}
