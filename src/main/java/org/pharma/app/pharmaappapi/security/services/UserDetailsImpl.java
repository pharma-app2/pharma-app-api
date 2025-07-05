package org.pharma.app.pharmaappapi.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pharma.app.pharmaappapi.security.models.Role;
import org.pharma.app.pharmaappapi.security.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDetailsImpl implements UserDetails {
    // differentiates serializable objects, because UserDetails is serializable (good practice)
    @Serial
    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    private UUID id;

    private String fullName;
    private String email;

    @JsonIgnore // don't serialize password (sensitive information)
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user) {
        UUID id = user.getId();
        String fullName = user.getFullName();
        String email = user.getEmail();
        String password = user.getPassword();
        Role role = user.getRole();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getName().name());
        List<SimpleGrantedAuthority> authorities = List.of(authority);

        return new UserDetailsImpl(id, fullName, email, password, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
