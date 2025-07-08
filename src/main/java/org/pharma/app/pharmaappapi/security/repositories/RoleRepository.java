package org.pharma.app.pharmaappapi.security.repositories;

import org.pharma.app.pharmaappapi.security.models.users.Role;
import org.pharma.app.pharmaappapi.security.models.users.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findFirstByName(RoleName name);
}
