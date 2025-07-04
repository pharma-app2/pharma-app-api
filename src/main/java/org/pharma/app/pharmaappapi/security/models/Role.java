package org.pharma.app.pharmaappapi.security.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {
    public Role(RoleName name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    private UUID id;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private RoleName name;
}
