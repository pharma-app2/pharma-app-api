package org.pharma.app.pharmaappapi.controllers;

import jakarta.validation.Valid;
import org.pharma.app.pharmaappapi.payloads.availabilityDTOs.AvailabilityParameters;
import org.pharma.app.pharmaappapi.payloads.availabilityDTOs.AvailabilityCreateDTO;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.pharma.app.pharmaappapi.services.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/pharmacists/{pharmacist_id}/availabilities")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<Set<AvailabilityCreateDTO>> getAvailabilities(
            @PathVariable UUID pharmacist_id,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        AvailabilityParameters params = new AvailabilityParameters
                .Builder()
                .withStartDate(startDate)
                .withEndDate(endDate)
                .build();
        Set<AvailabilityCreateDTO> availabilities = availabilityService.getAvailabilities(pharmacist_id, params);

        return ResponseEntity.status(HttpStatus.OK).body(availabilities);
    }

    @GetMapping("/pharmacists/availabilities")
    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    public ResponseEntity<?> getOwnAvailabilities() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/pharmacists/availabilities")
    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    public ResponseEntity<?> createAvailability(
            Authentication authentication,
            @RequestBody @Valid AvailabilityCreateDTO availabilityDTO) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        availabilityService.createAvailability(userId, availabilityDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
