package org.pharma.app.pharmaappapi.repositories.healthPlanRepository;

import java.util.UUID;

public interface HealthPlanProjection {
    UUID getId();
    String getPlanName();
    String getAnsRegistrationCode();
    String getOperatorName();
    String getContractType();
    String getPlanType();
    String getCoverageScope();
}
