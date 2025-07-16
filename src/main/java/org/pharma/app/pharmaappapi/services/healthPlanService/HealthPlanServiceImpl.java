package org.pharma.app.pharmaappapi.services.healthPlanService;

import org.pharma.app.pharmaappapi.repositories.healthPlanRepository.HealthPlanProjection;
import org.pharma.app.pharmaappapi.repositories.healthPlanRepository.HealthPlanRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class HealthPlanServiceImpl implements HealthPlanService {
    private final HealthPlanRepository healthPlanRepository;

    public HealthPlanServiceImpl(HealthPlanRepository healthPlanRepository) {
        this.healthPlanRepository = healthPlanRepository;
    }

    @Override
    public Set<HealthPlanProjection> getAllHealthPlans() {
        return healthPlanRepository.findHealthPlans();
    }
}
