package org.pharma.app.pharmaappapi.security.repositories;

import org.pharma.app.pharmaappapi.security.models.RoleName;
import org.pharma.app.pharmaappapi.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<User, UUID> {
    Boolean existsByEmailAndRole_Name(String email, RoleName roleName);
    Boolean existsByPatient_CpfAndRole_Name(String cpf, RoleName roleName);
}
