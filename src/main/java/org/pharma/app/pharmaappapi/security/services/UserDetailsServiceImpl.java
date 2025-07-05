package org.pharma.app.pharmaappapi.security.services;

import org.pharma.app.pharmaappapi.security.models.User;
import org.pharma.app.pharmaappapi.security.repositories.AuthRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthRepository authRepository;

    public UserDetailsServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = authRepository.findFirstByEmail(email);

        if (user != null) {
            return UserDetailsImpl.build(user);
        }

        throw new UsernameNotFoundException("User not found");
    }
}
