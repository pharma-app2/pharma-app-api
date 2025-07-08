package org.pharma.app.pharmaappapi.security.controllers;

import jakarta.validation.Valid;
import org.pharma.app.pharmaappapi.security.DTOs.*;
import org.pharma.app.pharmaappapi.security.models.users.RoleName;
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
    public ResponseEntity<LoginResponse> signInPatient(@RequestBody @Valid SignInDTO signInDTO) {
        LoginResponse response = authService.signInUser(signInDTO, RoleName.ROLE_PATIENT);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, response.jwtCookie().toString())
                .body(response);
    }

    @PostMapping("/auth/signin/pharmacist")
    public ResponseEntity<LoginResponse> signInPharmacist(@RequestBody @Valid SignInDTO signInDTO) {
        LoginResponse response = authService.signInUser(signInDTO, RoleName.ROLE_PHARMACIST);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, response.jwtCookie().toString())
                .body(response);
    }

    @PostMapping("/auth/signup/pharmacist")
    public ResponseEntity<?> signUpPharmacist(@RequestBody @Valid SignUpPharmacistDTO signUpDTO) {
        authService.signUpPharmacist(signUpDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
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
