package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.application.mapper.ProductMapper;
import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.api.dto.CreateProductRequest;
import com.techsolution.product_service.api.dto.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductUseCase {
    private static final Logger logger = LoggerFactory.getLogger(CreateProductUseCase.class);
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public CreateProductUseCase(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse execute(CreateProductRequest request) {
        logger.debug("Executing CreateProductUseCase for product: {}", request.name());
        
        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );

        Product savedProduct = productRepository.save(product);
        logger.debug("Product saved with id: {}", savedProduct.getId());

        return productMapper.toResponse(savedProduct);
    }
}

