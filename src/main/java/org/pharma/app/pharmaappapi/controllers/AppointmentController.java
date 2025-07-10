package org.pharma.app.pharmaappapi.controllers;

import jakarta.validation.Valid;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.pharma.app.pharmaappapi.security.services.UserDetailsServiceImpl;
import org.pharma.app.pharmaappapi.services.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointments")
    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_PHARMACIST')")
    public ResponseEntity<CreateAppointmentDTO> createAppointment(
            Authentication authentication,
            @Valid @RequestBody CreateAppointmentDTO createAppointmentDTO
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        CreateAppointmentDTO appointment = appointmentService.createAppointment(userDetails, createAppointmentDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }
}
