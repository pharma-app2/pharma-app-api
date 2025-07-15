package org.pharma.app.pharmaappapi.security.repositories;

public interface UserInfoProjection {
    String getEmail();
    String getFullName();
    String getRoleName();
    String getCpf();
    String getBirthday();
    String getCrf();
}
