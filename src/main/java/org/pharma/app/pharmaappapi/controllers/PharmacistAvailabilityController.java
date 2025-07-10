package org.pharma.app.pharmaappapi.controllers;

import jakarta.validation.Valid;
import org.pharma.app.pharmaappapi.payloads.pharmacistAvailabilityDTOs.PharmacistAvailabilityDTO;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.pharma.app.pharmaappapi.services.PharmacistAvailabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PharmacistAvailabilityController {
    private final PharmacistAvailabilityService pharmacistAvailabilityService;

    public PharmacistAvailabilityController(PharmacistAvailabilityService pharmacistAvailabilityService) {
        this.pharmacistAvailabilityService = pharmacistAvailabilityService;
    }

    @PostMapping("/pharmacist-availabilities")
    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    public ResponseEntity<?> createPharmacistAvailability(
            Authentication authentication,
            @RequestBody @Valid PharmacistAvailabilityDTO pharmacistAvailabilityDTO) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println(userDetails.getAuthorities().iterator().next().getAuthority());
        UUID userId = userDetails.getId();

        pharmacistAvailabilityService.createPharmacistAvailability(userId, pharmacistAvailabilityDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
