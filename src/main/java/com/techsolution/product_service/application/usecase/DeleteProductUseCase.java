package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.domain.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteProductUseCase {
    private static final Logger logger = LoggerFactory.getLogger(DeleteProductUseCase.class);
    
    private final ProductRepository productRepository;

    public DeleteProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void execute(UUID id) {
        logger.debug("Executing DeleteProductUseCase (deactivate) for product id: {}", id);
        
        if (!productRepository.existsByIdAndActive(id)) {
            throw new ResourceNotFoundException("Product", id);
        }

        productRepository.deactivateById(id);
        logger.info("Product deactivated successfully with id: {}", id);
    }
}

