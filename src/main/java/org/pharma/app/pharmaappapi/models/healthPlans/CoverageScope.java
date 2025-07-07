package org.pharma.app.pharmaappapi.models.healthPlans;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coverage_scopes")
@EqualsAndHashCode
@ToString
public class CoverageScope {
    public CoverageScope(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    private UUID id;

    @NotNull
    @NotBlank
    @Column(name = "name", nullable = false, unique = true)
    @Size(
            min = 3,
            max = 50
    )
    private String name;
}
