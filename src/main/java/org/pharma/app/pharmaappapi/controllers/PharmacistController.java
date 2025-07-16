package org.pharma.app.pharmaappapi.controllers;

import jakarta.validation.Valid;
import org.apache.tomcat.util.http.parser.Authorization;
import org.pharma.app.pharmaappapi.payloads.pharmacistDTOs.PharmacistDTO;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.pharma.app.pharmaappapi.services.PharmacistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PharmacistController {
    private final PharmacistService pharmacistService;

    public PharmacistController(PharmacistService pharmacistService) {
        this.pharmacistService = pharmacistService;
    }

    @PutMapping("/me/profile")
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
