package org.pharma.app.pharmaappapi.controllers;

import jakarta.validation.Valid;
import org.pharma.app.pharmaappapi.payloads.pharmacistDTOs.PharmacistDTO;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.pharma.app.pharmaappapi.services.pharmacistService.PharmacistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PharmacistController {
    private final PharmacistService pharmacistService;

    public PharmacistController(PharmacistService pharmacistService) {
        this.pharmacistService = pharmacistService;
    }

    @GetMapping("/pharmacists/me")
    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    public ResponseEntity<PharmacistDTO> getPharmacistProfile(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        PharmacistDTO savedPharmacistDTO = pharmacistService.getPharmacistProfile(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedPharmacistDTO);
    }

    @PutMapping("/pharmacists/me")
    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    public ResponseEntity<PharmacistDTO> updatePharmacistProfile(
            Authentication authentication,
            @RequestBody @Valid PharmacistDTO pharmacistDTO) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        PharmacistDTO savedPharmacistDTO = pharmacistService.updatePharmacistProfile(userId, pharmacistDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedPharmacistDTO);
    }
}
