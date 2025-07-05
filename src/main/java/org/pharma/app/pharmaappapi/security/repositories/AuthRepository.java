package org.pharma.app.pharmaappapi.security.repositories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.pharma.app.pharmaappapi.security.models.RoleName;
import org.pharma.app.pharmaappapi.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<User, UUID> {
    Boolean existsByEmailAndRole_Name(String email, RoleName roleName);
    Boolean existsByPatient_CpfAndRole_Name(String cpf, RoleName roleName);

//    @Query(
//            nativeQuery = true,
//            value = "SELECT "
//    )
    // TODO: criar query com inner join com a role (por causa do fetch lazy, no UserDetailsService o .getRole() faz uma nova consulta. O inner join remove essa necessidade (ver Gemini))
    User findFirstByEmail(String email);
}
