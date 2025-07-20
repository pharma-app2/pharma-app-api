package org.pharma.app.pharmaappapi.controllers;

import jakarta.validation.Valid;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.AppointmentDTO;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentPatientProjection;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentPharmacistProjection;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.pharma.app.pharmaappapi.services.appointmentService.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointments/availabilities/{availabilityId}")
    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_PHARMACIST')")
    public ResponseEntity<CreateAppointmentDTO> createAppointment(
            Authentication authentication,
            @Valid @RequestBody CreateAppointmentDTO createAppointmentDTO,
            @PathVariable UUID availabilityId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        createAppointmentDTO.setAvailabilityId(availabilityId);

        CreateAppointmentDTO appointment = appointmentService.createAppointment(userDetails, createAppointmentDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @GetMapping("/appointments/patient/future")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<Set<AppointmentPatientProjection>> getPatientFutureAppointments(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Set<AppointmentPatientProjection> appointments = appointmentService.getPatientFutureAppointments(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointments);
    }

    @GetMapping("pharmacists/me/appointments")
    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    public ResponseEntity<Set<AppointmentDTO>> getPharmacistFutureAppointments(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Set<AppointmentDTO> appointments = appointmentService.getPharmacistFutureAppointments(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointments);
    }

    @DeleteMapping("pharmacists/me/appointments/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    public ResponseEntity<?> deleteAppointment(Authentication authentication, @PathVariable UUID appointmentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        appointmentService.deleteAppointment(userId, appointmentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
