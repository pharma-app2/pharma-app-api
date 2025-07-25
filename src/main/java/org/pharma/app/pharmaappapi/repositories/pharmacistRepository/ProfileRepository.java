package org.pharma.app.pharmaappapi.repositories.pharmacistRepository;

import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Pharmacist, UUID>, ProfileRepositoryCustom {
    Optional<Pharmacist> findFirstByUser_Id(UUID userId);

    @Query(
            nativeQuery = true,
            value = "SELECT u.full_name AS fullName, u.email, p.crf, p.accepts_remote, hp.plan_name AS planName, " +
                    "pl.address, pl.phone1, pl.phone2, pl.phone3, pl.ibge_api_city_id AS ibgeApiCityId, " +
                    "pl.ibge_api_city AS ibgeApiCity, pl.ibge_api_state AS ibgeApiState " +
                    "FROM users u " +
                    "LEFT JOIN pharmacists p ON u.id = p.user_id " +
                    "LEFT JOIN health_plans_pharmacists hpp ON hpp.pharmacist_id = p.id " +
                    "LEFT JOIN health_plans hp ON hp.id = hpp.health_plan_id " +
                    "LEFT JOIN pharmacist_locations pl ON pl.pharmacist_id = p.id " +
                    "WHERE u.id = :userIdPlaceholder;"
    )
    Set<PharmacistProfileFlatProjection> findPharmacistProfile(@Param("userIdPlaceholder") UUID userId);
}
