package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.domain.exception.ResourceNotFoundException;
import com.techsolution.product_service.interfaces.dto.ProductResponse;
import com.techsolution.product_service.interfaces.dto.UpdateProductRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateProductUseCase {
    private static final Logger logger = LoggerFactory.getLogger(UpdateProductUseCase.class);
    
    private final ProductRepository productRepository;

    public UpdateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @CacheEvict(value = {"product", "productsPage"}, key = "#id.toString()", allEntries = true)
    public ProductResponse execute(UUID id, UpdateProductRequest request) {
        logger.debug("Executing UpdateProductUseCase for product id: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.update(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );

        Product updatedProduct = productRepository.save(product);
        logger.debug("Product updated successfully with id: {} - cache invalidated and updated", id);

        return toResponse(updatedProduct);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }
}



