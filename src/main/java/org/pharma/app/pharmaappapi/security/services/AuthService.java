package org.pharma.app.pharmaappapi.security.services;

import org.pharma.app.pharmaappapi.security.DTOs.users.*;
import org.pharma.app.pharmaappapi.security.models.users.RoleName;
import org.pharma.app.pharmaappapi.security.repositories.UserInfoProjection;
import org.springframework.http.ResponseCookie;

public interface AuthService {
    void signUpPatient(SignUpPatientDTO signUpDTO);
    void signUpPharmacist(SignUpPharmacistDTO signUpDTO);
    LoginResponse signInUser(SignInDTO signInDTO, RoleName roleName);
    UserInfoProjection getCurrentUserInfoByUserDetails(UserDetailsImpl userDetails);
    ResponseCookie getCleanJwtCookie();
}
