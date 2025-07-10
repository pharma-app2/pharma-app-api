package org.pharma.app.pharmaappapi.security.services;

import org.pharma.app.pharmaappapi.security.models.users.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RoleAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        RoleUsernamePasswordAuthenticationToken customToken = (RoleUsernamePasswordAuthenticationToken) authentication;
        RoleName role = customToken.getRole();

        UserDetails userDetails = userDetailsService.loadUserByUsernameAndRole(username, role);

        // Verifica a senha
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new UsernameNotFoundException("User not found");
        }

        return new RoleUsernamePasswordAuthenticationToken(userDetails, null, role);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(RoleUsernamePasswordAuthenticationToken.class);
    }
}
