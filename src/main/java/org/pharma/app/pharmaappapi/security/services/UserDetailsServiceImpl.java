package org.pharma.app.pharmaappapi.security.services;

import org.pharma.app.pharmaappapi.security.models.users.RoleName;
import org.pharma.app.pharmaappapi.security.models.users.User;
import org.pharma.app.pharmaappapi.security.repositories.AuthRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthRepository authRepository;

    public UserDetailsServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Since we're using @Transactional, the JPA session doesn't close after this query (so we can use .getRole() at UserDetailsImpl)
        User user = authRepository.findFirstByEmail(email, RoleName.ROLE_PATIENT.name());

        if (user != null) {
            return UserDetailsImpl.build(user);
        }

        throw new UsernameNotFoundException("User not found");
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsernameAndRole(String email, RoleName role) throws UsernameNotFoundException {
        // Since we're using @Transactional, the JPA session doesn't close after this query (so we can use .getRole() at UserDetailsImpl)
        User user = authRepository.findFirstByEmail(email, role.name());

        if (user != null) {
            return UserDetailsImpl.build(user);
        }

        throw new UsernameNotFoundException("User not found");
    }
}
