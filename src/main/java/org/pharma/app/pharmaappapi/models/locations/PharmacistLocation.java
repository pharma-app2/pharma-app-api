package org.pharma.app.pharmaappapi.models.locations;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;

import java.util.UUID;

@Entity
@Table(name = "pharmacist_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PharmacistLocation {
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
            min = 8,
            max = 15,
            message = "Phone number must have between 8 and 15 characters"
    )
    @Column(name = "phone1", nullable = false, length = 15)
    @EqualsAndHashCode.Include
    private String phone1;

    @Size(
            min = 8,
            max = 15,
            message = "Phone number must have between 8 and 15 characters"
    )
    @Column(name = "phone2", length = 15)
    @EqualsAndHashCode.Include
    private String phone2;

    @Size(
            min = 8,
            max = 15,
            message = "Phone number must have between 8 and 15 characters"
    )
    @Column(name = "phone3", length = 15)
    @EqualsAndHashCode.Include
    private String phone3;

    @NotNull
    @NotBlank
    @Size(
            min = 10,
            max = 255,
            message = "Address must have between 10 and 255 characters"
    )
    @Column(name = "address", nullable = false)
    @EqualsAndHashCode.Include
    private String address;

    @NotNull
    @Column(name = "ibge_api_city_id", nullable = false)
    @EqualsAndHashCode.Include
    private Integer ibgeApiIdentifierCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacist_id", referencedColumnName = "id", nullable = false)
    private Pharmacist pharmacist;
}
