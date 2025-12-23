package com.techsolution.product_service.api.exception;

/**
 * Enum que representa os tipos de erro tratados pelo GlobalExceptionHandler.
 * Centraliza as mensagens de erro para evitar strings mágicas no código.
 */
public enum ErrorType {
    RESOURCE_NOT_FOUND("Resource Not Found"),
    BUSINESS_ERROR("Business Error"),
    VALIDATION_ERROR("Validation Error"),
    INVALID_ARGUMENT("Invalid Argument"),
    INTERNAL_SERVER_ERROR("Internal Server Error");

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

