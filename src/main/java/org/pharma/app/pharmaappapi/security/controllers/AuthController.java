package org.pharma.app.pharmaappapi.security.controllers;

import jakarta.validation.Valid;
import org.pharma.app.pharmaappapi.security.DTOs.users.*;
import org.pharma.app.pharmaappapi.security.models.users.RoleName;
import org.pharma.app.pharmaappapi.security.services.AuthService;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_PATIENT')")
    public ResponseEntity<UserInfoDTO> getCurrentUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserInfoDTO currentUserInfo = authService.getCurrentUserInfoByUserDetails(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(currentUserInfo);
    }

    @PostMapping("/auth/signup/patient")
    public ResponseEntity<UserInfoDTO> signUpPatient(@RequestBody @Valid SignUpPatientDTO signUpDTO) {
        UserInfoDTO userInfoDTO = authService.signUpPatient(signUpDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(userInfoDTO);
    }

    @PostMapping("/auth/signin/patient")
    public ResponseEntity<UserInfoDTO> signInPatient(@RequestBody @Valid SignInDTO signInDTO) {
        LoginResponse response = authService.signInUser(signInDTO, RoleName.ROLE_PATIENT);

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setEmail(response.email());
        userInfoDTO.setRoleName(response.role());

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, response.jwtCookie().toString())
                .body(userInfoDTO);
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

    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_PATIENT')")
    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser() {
        ResponseCookie cleanJwtCookie = authService.getCleanJwtCookie();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cleanJwtCookie.toString())
                .body(null);
    }
}
