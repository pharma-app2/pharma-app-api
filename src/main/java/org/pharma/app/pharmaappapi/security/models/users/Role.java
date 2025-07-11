package org.pharma.app.pharmaappapi.security.models.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.pharma.app.pharmaappapi.models.healthPlans.HealthPlan;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "roles")
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "name",
                        name = "uk_role_name"
                )
        })
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Role {
    public Role(RoleName name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    private UUID id;

//    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private RoleName name;
}
