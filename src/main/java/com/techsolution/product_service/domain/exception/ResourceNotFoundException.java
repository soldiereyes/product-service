package com.techsolution.product_service.domain.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, UUID id) {
        super(String.format("%s with id %s not found", resourceName, id));
    }
}



