package com.techsolution.product_service.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    PageResult<Product> findAll(int page, int size);
    void deactivateById(UUID id);
    boolean existsById(UUID id);
    boolean existsByIdAndActive(UUID id);
    
    record PageResult<T>(
            List<T> content,
            long totalElements,
            int totalPages
    ) {
    }
}




