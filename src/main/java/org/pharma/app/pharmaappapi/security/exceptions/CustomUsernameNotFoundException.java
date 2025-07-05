package org.pharma.app.pharmaappapi.security.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUsernameNotFoundException extends UsernameNotFoundException {
    public CustomUsernameNotFoundException(String fieldName, String fieldValue) {
        super(String.format("User with %s %s not found.", fieldName, fieldValue));
    }
}
