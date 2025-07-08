package org.pharma.app.pharmaappapi.models.healthPlans;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "health_plans",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ans_registration_code",
                        columnNames = "ans_registration_code"
                )
        }
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class HealthPlan {
    public HealthPlan(String planName) {
        this.planName = planName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @NotNull
    @NotBlank
    @Column(name = "plan_name", nullable = false)
    @Size(
            min = 3,
            max = 100
    )
    @ToString.Include
    private String planName;

    @NotNull
    @NotBlank
    @Column(name = "ans_registration_code", nullable = false)
    @Size(
            min = 3,
            max = 30
    )
    @ToString.Include
    private String ansRegistrationCode;

    @NotNull
    @Column(name = "is_active", nullable = false)
    @ToString.Include
    private Boolean isActive;

    // we won't use merge and persist cascade because these are lookup tables. We should not create a new lookup table when creating HealthPlan table (we just use these pre-existing lookup tables)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_type_id", referencedColumnName = "id", nullable = false)
    private ContractType contractType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coverage_scope_id", referencedColumnName = "id", nullable = false)
    private CoverageScope coverageScope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_type_id", referencedColumnName = "id", nullable = false)
    private PlanType planType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", referencedColumnName = "id", nullable = false)
    private Operator operator;

    // mappedBy avoid JPA to creating a second join table (we've already created one). Pharmacist owns the relationship
    @ManyToMany(mappedBy = "healthPlans", fetch = FetchType.LAZY)
    private Set<Pharmacist> pharmacists = new HashSet<>();
}
