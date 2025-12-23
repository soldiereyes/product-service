package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.application.mapper.ProductMapper;
import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.api.dto.PageResponse;
import com.techsolution.product_service.api.dto.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListProductsUseCase {
    private static final Logger logger = LoggerFactory.getLogger(ListProductsUseCase.class);
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ListProductsUseCase(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductResponse> execute() {
        logger.debug("Executing ListProductsUseCase");
        
        List<Product> products = productRepository.findAll();
        logger.debug("Found {} products", products.size());

        return productMapper.toResponseList(products);
    }

    public PageResponse<ProductResponse> execute(int page, int size) {
        logger.debug("Executing ListProductsUseCase with pagination - page: {}, size: {}", page, size);
        
        ProductRepository.PageResult<Product> pageResult = productRepository.findAll(page, size);
        
        List<ProductResponse> content = productMapper.toResponseList(pageResult.content());
        
        logger.debug("Found {} products (page {} of {})", 
                content.size(), page, pageResult.totalPages());

        return PageResponse.of(content, page, size, pageResult.totalElements());
    }
}