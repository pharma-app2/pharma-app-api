package org.pharma.app.pharmaappapi.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.pharma.app.pharmaappapi.payloads.responseDTOs.APIExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> customConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();

        e.getConstraintViolations().forEach(violation -> {
            String completeFieldPath = violation.getPropertyPath().toString();
            String fieldName = completeFieldPath.substring(completeFieldPath.lastIndexOf(".") + 1);
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> customMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<APIExceptionResponse> customConflictAPIException(ConflictException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.CONFLICT.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<APIExceptionResponse> customResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.CONFLICT.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIExceptionResponse> customResourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.NOT_FOUND.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<APIExceptionResponse> customUnprocessableEntityException(UnprocessableEntityException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.UNPROCESSABLE_ENTITY.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(apiResponse);
    }
}
