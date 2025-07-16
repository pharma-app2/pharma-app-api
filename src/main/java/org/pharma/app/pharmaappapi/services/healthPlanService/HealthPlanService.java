package org.pharma.app.pharmaappapi.services.healthPlanService;

import org.pharma.app.pharmaappapi.repositories.healthPlanRepository.HealthPlanProjection;

import java.util.Set;

public interface HealthPlanService {
    Set<HealthPlanProjection> getAllHealthPlans();
}
