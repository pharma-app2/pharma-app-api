package org.pharma.app.pharmaappapi.controllers;

import org.pharma.app.pharmaappapi.payloads.appointmentModalityDTOs.AppointmentModalityDTO;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.pharma.app.pharmaappapi.services.appointmentModalityService.AppointmentModalityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AppointmentModalityController {
    private final AppointmentModalityService appointmentModalityService;

    public AppointmentModalityController(AppointmentModalityService appointmentModalityService) {
        this.appointmentModalityService = appointmentModalityService;
    }

    @GetMapping("/pharmacists/{pharmacistId}/appointments-modalities")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<Set<AppointmentModalityDTO>> getAppointmentModalitiesFromPharmacist(@PathVariable UUID pharmacistId) {
        Set<AppointmentModalityDTO> modalities = appointmentModalityService.getAppointmentModalitiesFromPharmacist(pharmacistId);

        return ResponseEntity.status(HttpStatus.OK).body(modalities);
    }

    @PostMapping("/appointments-modalities/{appointmentModalityId}")
    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    public ResponseEntity<?> createAppointmentModalityForPharmacist(
            Authentication authentication,
            @PathVariable UUID appointmentModalityId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        appointmentModalityService.createAppointmentModalityForPharmacist(userId, appointmentModalityId);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
