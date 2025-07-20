package org.pharma.app.pharmaappapi.controllers;

import org.pharma.app.pharmaappapi.payloads.patientDTOs.PatientSearchByNameDTO;
import org.pharma.app.pharmaappapi.services.patientService.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients/search")
    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    public ResponseEntity<Set<PatientSearchByNameDTO>> getProfiles(@RequestParam(name = "name", required = false) String name) {

        Set<PatientSearchByNameDTO> profiles = patientService.getProfilesByName(name);

        return ResponseEntity.status(HttpStatus.OK).body(profiles);
    }
}
