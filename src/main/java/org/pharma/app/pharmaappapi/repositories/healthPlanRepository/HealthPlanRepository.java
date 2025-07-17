package org.pharma.app.pharmaappapi.repositories.healthPlanRepository;

import org.pharma.app.pharmaappapi.models.healthPlans.HealthPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface HealthPlanRepository extends JpaRepository<HealthPlan, UUID> {
    @Query(
            nativeQuery = true,
            value = "SELECT hp.id, hp.plan_name AS planName, hp.ans_registration_code AS ansRegistrationCode, " +
                    "o.name AS operatorName, ct.name AS contractType, pt.name AS planType, cs.name AS coverageScope " +
                    "FROM health_plans hp " +
                    "JOIN operators o ON o.id = hp.operator_id " +
                    "JOIN contract_types ct ON ct.id = hp.contract_type_id " +
                    "JOIN plan_types pt ON pt.id = hp.plan_type_id " +
                    "JOIN coverage_scopes cs ON cs.id = hp.coverage_scope_id " +
                    "ORDER BY hp.plan_name;"
    )
    Set<HealthPlanProjection> findHealthPlans();

    Set<HealthPlan> findAllByPlanNameIn(Set<String> planNames);
}
