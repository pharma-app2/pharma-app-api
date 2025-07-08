package org.pharma.app.pharmaappapi.security.services;

import lombok.Getter;
import org.pharma.app.pharmaappapi.security.models.users.RoleName;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class RoleUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final RoleName role;

    // Before authentication
    public RoleUsernamePasswordAuthenticationToken(Object principal, Object credentials, RoleName role) {
        super(principal, credentials);
        this.role = role;
    }

    // After successful authentication
    public RoleUsernamePasswordAuthenticationToken(Object principal, Object credentials, RoleName role, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.role = role;
    }
}
