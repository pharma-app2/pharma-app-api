package org.pharma.app.pharmaappapi.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s com %s %s já existe.", resourceName, fieldName, fieldValue));
    }
}
