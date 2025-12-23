package com.techsolution.product_service.api.controller;

import org.springframework.stereotype.Component;

/**
 * Validador de parâmetros de paginação.
 * Extrai a lógica de validação do controller para melhor separação de responsabilidades.
 */
@Component
public class PaginationValidator {

    /**
     * Valida os parâmetros de paginação.
     *
     * @param page número da página
     * @param size tamanho da página
     * @return mensagem de erro se inválido, null se válido
     */
    public String validate(int page, int size) {
        if (page < PaginationConstants.DEFAULT_PAGE) {
            return String.format("Page must be greater than or equal to %d", PaginationConstants.DEFAULT_PAGE);
        }
        
        if (size < PaginationConstants.MIN_SIZE || size > PaginationConstants.MAX_SIZE) {
            return String.format("Size must be between %d and %d", 
                    PaginationConstants.MIN_SIZE, PaginationConstants.MAX_SIZE);
        }
        
        return null;
    }
}

