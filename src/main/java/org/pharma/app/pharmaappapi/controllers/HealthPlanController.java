package org.pharma.app.pharmaappapi.controllers;

import org.pharma.app.pharmaappapi.repositories.healthPlanRepository.HealthPlanProjection;
import org.pharma.app.pharmaappapi.services.healthPlanService.HealthPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class HealthPlanController {
    private final HealthPlanService healthPlanService;

    public HealthPlanController(HealthPlanService healthPlanService) {
        this.healthPlanService = healthPlanService;
    }

    @GetMapping("/health_plans")
    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    public ResponseEntity<Set<HealthPlanProjection>> getAllHealthPlans() {
        Set<HealthPlanProjection> healthPlans = healthPlanService.getAllHealthPlans();

        return ResponseEntity.status(HttpStatus.CREATED).body(healthPlans);
    }
}
