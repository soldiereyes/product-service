package com.techsolution.product_service.interfaces.controller;

import com.techsolution.product_service.application.usecase.CreateProductUseCase;
import com.techsolution.product_service.application.usecase.DeleteProductUseCase;
import com.techsolution.product_service.application.usecase.GetProductByIdUseCase;
import com.techsolution.product_service.application.usecase.ListProductsUseCase;
import com.techsolution.product_service.application.usecase.UpdateProductUseCase;
import com.techsolution.product_service.interfaces.dto.CreateProductRequest;
import com.techsolution.product_service.interfaces.dto.ProductResponse;
import com.techsolution.product_service.interfaces.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    public ProductController(
            CreateProductUseCase createProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            GetProductByIdUseCase getProductByIdUseCase,
            ListProductsUseCase listProductsUseCase,
            DeleteProductUseCase deleteProductUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        logger.info("Creating product with name: {}", request.name());
        ProductResponse response = createProductUseCase.execute(request);
        logger.info("Product created successfully with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        logger.info("Updating product with id: {}", id);
        ProductResponse response = updateProductUseCase.execute(id, request);
        logger.info("Product updated successfully with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        logger.info("Getting product by id: {}", id);
        ProductResponse response = getProductByIdUseCase.execute(id);
        logger.info("Product found with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> list() {
        logger.info("Listing all products");
        List<ProductResponse> response = listProductsUseCase.execute();
        logger.info("Found {} products", response.size());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        logger.info("Deleting product with id: {}", id);
        deleteProductUseCase.execute(id);
        logger.info("Product deleted successfully with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}



