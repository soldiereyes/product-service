package com.techsolution.product_service.infrastructure.persistence;

import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.infrastructure.persistence.entity.ProductEntity;
import com.techsolution.product_service.infrastructure.persistence.jpa.JpaProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductRepositoryImpl implements ProductRepository {
    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryImpl.class);
    
    private final JpaProductRepository jpaProductRepository;

    public ProductRepositoryImpl(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }

    @Override
    public Product save(Product product) {
        logger.debug("Saving product with id: {}", product.getId());
        ProductEntity entity = toEntity(product);
        ProductEntity savedEntity = jpaProductRepository.save(entity);
        logger.debug("Product saved successfully with id: {}", savedEntity.getId());
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        logger.debug("Finding product by id: {}", id);
        Optional<Product> result = jpaProductRepository.findById(id)
                .map(this::toDomain);
        logger.debug("Product {} found: {}", id, result.isPresent());
        return result;
    }

    @Override
    public List<Product> findAll() {
        logger.debug("Finding all products");
        List<Product> products = jpaProductRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
        logger.debug("Found {} products", products.size());
        return products;
    }

    @Override
    public void deleteById(UUID id) {
        logger.debug("Deleting product by id: {}", id);
        jpaProductRepository.deleteById(id);
        logger.debug("Product deleted successfully with id: {}", id);
    }

    @Override
    public boolean existsById(UUID id) {
        logger.debug("Checking if product exists with id: {}", id);
        boolean exists = jpaProductRepository.existsById(id);
        logger.debug("Product {} exists: {}", id, exists);
        return exists;
    }

    private ProductEntity toEntity(Product product) {
        return new ProductEntity(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }

    private Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStockQuantity()
        );
    }
}