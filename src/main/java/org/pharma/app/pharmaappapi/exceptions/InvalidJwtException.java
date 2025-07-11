package org.pharma.app.pharmaappapi.exceptions;

public class InvalidJwtException extends RuntimeException {
    public InvalidJwtException(String message, Exception e) {
        super(message, e);
    }
}
