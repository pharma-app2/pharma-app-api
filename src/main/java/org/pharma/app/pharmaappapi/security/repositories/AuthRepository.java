package org.pharma.app.pharmaappapi.security.repositories;

import org.pharma.app.pharmaappapi.security.models.users.RoleName;
import org.pharma.app.pharmaappapi.security.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<User, UUID> {
    Boolean existsByEmailAndRole_Name(String email, RoleName roleName);
    Boolean existsByPatient_CpfAndRole_Name(String cpf, RoleName roleName);

    @Query(
            nativeQuery = true,
            value = "SELECT u.*, r.name FROM users u " +
                    "INNER JOIN roles r ON u.role_id = r.id " +
                    "WHERE u.email = :emailPlaceholder AND r.name = :rolePlaceholder"
    )
    // We explicitly bring roles table (because we build our entity with Lazy initialization - see User model)
    User findFirstByEmailAndRole(@Param("emailPlaceholder") String email, @Param("rolePlaceholder") String role);

    @Query(
            nativeQuery = true,
            value = "SELECT u.full_name AS fullName, u.email, r.name AS roleName, pa.cpf, pa.birthday, ph.crf " +
                    "FROM users u " +
                    "INNER JOIN roles r ON u.role_id = r.id " +
                    "LEFT JOIN patients pa ON pa.user_id = u.id " +
                    "LEFT JOIN pharmacists ph ON ph.user_id = u.id " +
                    "WHERE u.id = :idPlaceholder;"
    )
    UserInfoProjection findUserInfosById(@Param("idPlaceholder") UUID id);
}
