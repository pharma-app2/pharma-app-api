package org.pharma.app.pharmaappapi.security.repositories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.pharma.app.pharmaappapi.security.models.RoleName;
import org.pharma.app.pharmaappapi.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<User, UUID> {
    Boolean existsByEmailAndRole_Name(String email, RoleName roleName);
    Boolean existsByPatient_CpfAndRole_Name(String cpf, RoleName roleName);

    @Query(
            nativeQuery = true,
            value = "SELECT u.*, r.name FROM users u INNER JOIN roles r ON u.role_id = r.id WHERE u.email = :emailPlaceholder"
    )
    // We explicitly bring roles table (because we build our entity with Lazy initialization - see User model)
    User findFirstByEmail(@Param("emailPlaceholder") String email);

    User findUserPatientOrPharmacistById(UUID id);
}
