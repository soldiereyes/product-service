package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.interfaces.dto.PageResponse;
import com.techsolution.product_service.interfaces.dto.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListProductsUseCase {
    private static final Logger logger = LoggerFactory.getLogger(ListProductsUseCase.class);
    
    private final ProductRepository productRepository;

    public ListProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> execute() {
        logger.debug("Executing ListProductsUseCase");
        
        List<Product> products = productRepository.findAll();
        logger.debug("Found {} products", products.size());

        return products.stream()
                .map(this::toResponse)
                .toList();
    }

    public PageResponse<ProductResponse> execute(int page, int size) {
        logger.debug("Executing ListProductsUseCase with pagination - page: {}, size: {}", page, size);
        
        ProductRepository.PageResult<Product> pageResult = productRepository.findAll(page, size);
        
        List<ProductResponse> content = pageResult.content().stream()
                .map(this::toResponse)
                .toList();
        
        logger.debug("Found {} products (page {} of {})", 
                content.size(), page, pageResult.totalPages());

        return PageResponse.of(content, page, size, pageResult.totalElements());
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