package org.pharma.app.pharmaappapi.repositories.healthPlanRepository;

import org.pharma.app.pharmaappapi.models.healthPlans.HealthPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HealthPlanRepository extends JpaRepository<HealthPlan, UUID> {
    Optional<HealthPlan> findFirstById(UUID id);
}
