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

@Entity
@Table(
        name = "pharmacists",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "crf",
                        name = "uk_pharmacists_crf"
                )
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Pharmacist {
    public Pharmacist(String crf) {
        this.crf = crf;
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
    @Size(
            min = 3,
            max = 20,
            message = "Field crf must have between 3 and 20 characters"
    )
    @Column(name = "crf", nullable = false)
    @ToString.Include
    private String crf;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "health_plans_pharmacists",
            joinColumns = @JoinColumn(
                    name = "pharmacist_id",
                    referencedColumnName = "id",
                    nullable = false
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "health_plan_id",
                    referencedColumnName = "id",
                    nullable = false
            ),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_pharmacist_health_plan_association",
                    columnNames = { "pharmacist_id", "health_plan_id" }
            )
    )
    private Set<HealthPlan> healthPlans = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "patients_pharmacists",
            joinColumns = @JoinColumn(
                    name = "pharmacist_id",
                    referencedColumnName = "id",
                    nullable = false
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "patient_id",
                    referencedColumnName = "id",
                    nullable = false
            ),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_pharmacist_patient_association",
                    columnNames = { "pharmacist_id", "patient_id" }
            )
    )
    private Set<Patient> patients = new HashSet<>();
}
