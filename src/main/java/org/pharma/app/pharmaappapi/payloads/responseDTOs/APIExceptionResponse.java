package org.pharma.app.pharmaappapi.payloads.responseDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIExceptionResponse {
    private String message;
    private Integer statusCode;
}
