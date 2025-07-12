package org.pharma.app.pharmaappapi.models.availabilities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.pharma.app.pharmaappapi.models.appointments.Appointment;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.pharma.app.pharmaappapi.validations.allowedDurations.AllowedDurations;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pharmacist_availabilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @NotNull
    @ToString.Include
    @Column(name = "start_time", columnDefinition = "TIMESTAMPTZ", nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @AllowedDurations
    @Column(name = "duration_minutes", columnDefinition = "INTEGER")
    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacist_id", referencedColumnName = "id", nullable = false)
    private Pharmacist pharmacist;

    @OneToOne(mappedBy = "availability")
    private Appointment appointment;
}
