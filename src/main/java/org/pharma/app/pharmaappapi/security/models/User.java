package org.pharma.app.pharmaappapi.security.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "email",
                        name = "uk_user_email"
                )
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class User {
    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 50,
            message = "Field full_name must have between 3 and 50 characters"
    )
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull
    @NotBlank
    @Email
    @Size(
            min = 3,
            max = 100,
            message = "Field email must have between 3 and 100 characters"
    )
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 255,
            message = "Field password must have between 3 and 255 characters"
    )
    @Column(name = "password", nullable = false)
    private String password;

    /*
    With Lazy, JPA search for Role at DB if and only if we explicitly call user.getRole()
    Otherwise (with Eager), everytime JPA search for User, it brings Role within. It can
    lead to issues like N+1 problem.
    It also doesn't use CascadeType.PERSIST because we don't want to create a Role if
    an object role inside User is a non-existent Role instance. Otherwise, if a developer
    do user.setRole(new Role("ROLE_SUPER_ADMIN")) and save the user, JPA would try to
    create this new role in db - we don't want that.
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude // exclude lazy initializations (because when toString() calls getRole() at an User instance, the JPA session is already closed due to lazy initialization. It leads to a LazyInitializationException)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = true)
    private Patient patient;
}
