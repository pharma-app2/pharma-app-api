package org.pharma.app.pharmaappapi.repositories.pharmacistRepository;

import java.util.Set;

public interface ProfileRepositoryCustom {
    Set<ProfileByParamsProjection> findProfilesByParams(
            String pharmacistName,
            String ibgeApiCity,
            String ibgeApiState,
            Boolean acceptsRemote
    );
}