package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.application.mapper.ProductMapper;
import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.domain.exception.ResourceNotFoundException;
import com.techsolution.product_service.api.dto.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetProductByIdUseCase {
    private static final Logger logger = LoggerFactory.getLogger(GetProductByIdUseCase.class);
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public GetProductByIdUseCase(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponse execute(UUID id) {
        logger.debug("Executing GetProductByIdUseCase for product id: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        logger.debug("Product found with id: {}", id);
        return productMapper.toResponse(product);
    }
}