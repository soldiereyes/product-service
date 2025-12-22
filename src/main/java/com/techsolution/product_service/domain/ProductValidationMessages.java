package com.techsolution.product_service.domain;

/**
 * Constantes para mensagens de validação do domínio Product.
 * Centraliza as mensagens para facilitar manutenção e internacionalização futura.
 */
public final class ProductValidationMessages {
    
    public static final String NAME_CANNOT_BE_NULL_OR_EMPTY = "Product name cannot be null or empty";
    public static final String DESCRIPTION_CANNOT_BE_NULL_OR_EMPTY = "Product description cannot be null or empty";
    public static final String PRICE_CANNOT_BE_NULL_OR_NEGATIVE = "Product price cannot be null or negative";
    public static final String STOCK_QUANTITY_CANNOT_BE_NULL_OR_NEGATIVE = "Product stock quantity cannot be null or negative";
    
    private ProductValidationMessages() {
        // Classe utilitária - não deve ser instanciada
    }
}

