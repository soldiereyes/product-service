package com.techsolution.product_service.application.mapper;

import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.interfaces.dto.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper responsável por converter entidades de domínio Product em DTOs ProductResponse.
 * Centraliza a lógica de mapeamento para evitar duplicação de código.
 */
@Component
public class ProductMapper {

    /**
     * Converte um Product do domínio para ProductResponse DTO.
     *
     * @param product entidade de domínio
     * @return DTO de resposta
     */
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }

    /**
     * Converte uma lista de Products do domínio para lista de ProductResponse DTOs.
     *
     * @param products lista de entidades de domínio
     * @return lista de DTOs de resposta
     */
    public List<ProductResponse> toResponseList(List<Product> products) {
        if (products == null) {
            return List.of();
        }
        
        return products.stream()
                .map(this::toResponse)
                .toList();
    }
}

