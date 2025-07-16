package org.pharma.app.pharmaappapi.payloads.healthPlanDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthPlanDTO {
    private UUID id;

    private String planName;

    private String ansRegistrationCode;

    private Boolean isActive;

    private UUID contractTypeId;

    private UUID coverageScopeID;

    private UUID planTypeId;

    private UUID operatorId;
}
