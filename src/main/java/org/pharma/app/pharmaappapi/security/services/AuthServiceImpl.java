package org.pharma.app.pharmaappapi.security.services;

import org.modelmapper.ModelMapper;
import org.pharma.app.pharmaappapi.exceptions.ResourceAlreadyExistsException;
import org.pharma.app.pharmaappapi.exceptions.UnprocessableEntityException;
import org.pharma.app.pharmaappapi.security.DTOs.users.*;
import org.pharma.app.pharmaappapi.security.jwt.JwtUtils;
import org.pharma.app.pharmaappapi.security.models.users.*;
import org.pharma.app.pharmaappapi.security.repositories.AuthRepository;
import org.pharma.app.pharmaappapi.security.repositories.RoleRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           PasswordEncoder passwordEncoder,
                           AuthRepository authRepository,
                           RoleRepository roleRepository,
                           JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void signUpPatient(SignUpPatientDTO signUpDTO) {
        String fullName = signUpDTO.getFullName();
        String email = signUpDTO.getEmail();
        String password = signUpDTO.getPassword();
        String passwordConfirmation = signUpDTO.getPasswordConfirmation();
        String cpf = signUpDTO.getCpf();

        Boolean patientExistsByEmail = authRepository.existsByEmailAndRole_Name(email, RoleName.ROLE_PATIENT);
        Boolean patientExistsByCpf = authRepository.existsByPatient_CpfAndRole_Name(cpf, RoleName.ROLE_PATIENT);

        if (patientExistsByCpf) throw new ResourceAlreadyExistsException("Patient", "cpf", cpf);
        if (patientExistsByEmail) throw new ResourceAlreadyExistsException("Patient", "email", email);
        if (!password.equals(passwordConfirmation)) throw new UnprocessableEntityException("Password and password confirmation don't match.");

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(fullName, email, encodedPassword);
        Patient patient = new Patient(cpf);

        user.setPatient(patient);
        patient.setUser(user);

        // We won't create a Role instance (like we did with Patient) because it would create a new Role - we don't want that
        Role role = roleRepository.findFirstByName(RoleName.ROLE_PATIENT);
        user.setRole(role);

        authRepository.save(user);
    }

    // TODO: THIS IS A TEMPORARY METHOD - PHARMACIST WILL HAVE ITS OWN API FOR SIGN UP IN FUTURE
    @Override
    public void signUpPharmacist(SignUpPharmacistDTO signUpDTO) {
        String fullName = signUpDTO.getFullName();
        String email = signUpDTO.getEmail();
        String password = signUpDTO.getPassword();
        String cpf = signUpDTO.getCrf();

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(fullName, email, encodedPassword);

        Pharmacist pharmacist = new Pharmacist(cpf);
        user.setPharmacist(pharmacist);
        pharmacist.setUser(user);

        // We won't create a Role instance (like we did with Patient) because it would create a new Role - we don't want that
        Role role = roleRepository.findFirstByName(RoleName.ROLE_PHARMACIST);
        user.setRole(role);

        authRepository.save(user);
    }

    @Override
    public LoginResponse signInUser(SignInDTO signInDTO, RoleName roleName) {
        String email = signInDTO.getEmail();
        String password = signInDTO.getPassword();

        RoleUsernamePasswordAuthToken authToken = new RoleUsernamePasswordAuthToken(email, password, roleName);
        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookieFromUserDetails(userDetails);

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new LoginResponse(userDetails.getId(), userDetails.getUsername(), role, jwtCookie);
    }

    @Override
    public UserInfoDTO getCurrentUserInfoByUserDetails(UserDetailsImpl userDetails) {
        UUID userId = userDetails.getId();

        return authRepository.findUserInfosById(userId);
    }

    @Override
    public ResponseCookie getCleanJwtCookie() {
        return jwtUtils.getCleanJwtCookie();
    }
}
