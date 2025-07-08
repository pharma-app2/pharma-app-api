package org.pharma.app.pharmaappapi.models.appointments;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "appointments_modality",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_appointments_modality_name",
                columnNames = "name"
        )
)
@Data
public class AppointmentModality {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private AppointmentModalityName name;
}
